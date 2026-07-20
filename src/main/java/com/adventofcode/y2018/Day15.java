package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 {
    private final static Comparator<Coordinate> BY_Y_THEN_X = Comparator.comparing(Coordinate::y)
            .thenComparing(Coordinate::x);
    private final Battle battle;

    public Day15(Battle battle) {
        this.battle = battle;
    }

    public Day15() throws IOException {
        this(Input.day15());
    }

    long part1() {
        BattleResult battleResult = battle(this.battle.copy(null));
        return (long) battleResult.completedRounds() * battleResult.battle().sumHpOfRemainingUnits();
    }

    long part2() {
        BattleResult battleResult = battle(this.battle.copy(null));
        int attackAdjustement = battleResult.defeatedUnits().stream()
                .filter(unit -> unit.unitKind() == UnitKind.ELF)
                .mapToInt(unit -> unit.enemiesHpAndMomentOfDefeat / unit.attacks + (unit.enemiesHpAndMomentOfDefeat % unit.attacks == 0 ? 0 : 1))
                .max()
                .orElseThrow();
        battleResult = battle(this.battle.copyWithElvesEnhancement(attackAdjustement));
        int[] bounds = findBounds(attackAdjustement, noElfKilled(battleResult) ? i -> i / 2 : i -> i * 2);
        for (attackAdjustement = bounds[0]; attackAdjustement <= bounds[1]; attackAdjustement += 2) {
            battleResult = battle(this.battle.copyWithElvesEnhancement(attackAdjustement));
            if (noElfKilled(battleResult)) {
                BattleResult battleResultPrev = battle(this.battle.copyWithElvesEnhancement(attackAdjustement - 1));
                if (noElfKilled(battleResultPrev)) {
                    return (long) battleResultPrev.battle().sumHpOfRemainingUnits() * battleResultPrev.completedRounds();
                }
                return (long) battleResult.battle().sumHpOfRemainingUnits() * battleResult.completedRounds();
            }
        }
        throw new RuntimeException();
    }

    private int[] findBounds(int currentBound, IntFunction<Integer> nextBoundToCheckProvider) {
        int current = currentBound;
        int next = nextBoundToCheckProvider.apply(current);
        while (true) {
            BattleResult battleResult = battle(this.battle.copyWithElvesEnhancement(next));
            if (!noElfKilled(battleResult)) return new int[]{Math.min(next, current), Math.max(next, current)};
            current = next;
            next = nextBoundToCheckProvider.apply(current);
        }
    }

    private boolean noElfKilled(BattleResult battleResult) {
        return battleResult.defeatedUnits.stream().noneMatch(u -> u.unitKind() == UnitKind.ELF);
    }

    private BattleResult battle(Battle battle) {
        int roundsCompleted = 0;
        List<Unit> defeatedUnits = new ArrayList<>();
        while (true) {
            RoundResult roundResult = round(battle);
            defeatedUnits.addAll(roundResult.defeatedUnits());
            if (!roundResult.roundCompleted()) {
                break;
            }
            roundsCompleted++;
            if (roundResult.roundDamage() + roundResult.roundMoves() == 0)
                break;
        }
        return new BattleResult(battle, roundsCompleted, defeatedUnits);
    }

    public record BattleResult(Battle battle, int completedRounds, List<Unit> defeatedUnits) {
    }


    private RoundResult round(Battle battle) {
        int roundMoves = 0;
        int roundDamage = 0;
        List<Map.Entry<Coordinate, Unit>> unitsInMoveOrder = battle.unitsDeployment()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(BY_Y_THEN_X))
                .collect(Collectors.toCollection(ArrayList::new));

        List<Unit> defeatedUnits = new ArrayList<>();
        while (!unitsInMoveOrder.isEmpty()) {
            Map.Entry<Coordinate, Unit> unit = unitsInMoveOrder.removeFirst();
            Coordinate unitFinalPosition = unit.getKey();
            Map.Entry<Coordinate, Unit> target = getTargets(battle, unit)
                    .stream()
                    .filter(targetDeployment -> isInRange(unit.getKey(), targetDeployment))
                    .min(Map.Entry.<Coordinate, Unit>comparingByValue(Comparator.comparing(Unit::hp))
                            .thenComparing(Map.Entry.comparingByKey(BY_Y_THEN_X)))
                    .orElse(null);
            if (target == null) {
                int[][] distances = distances(battle, unit);

                Coordinate targetLocation = getTargets(battle, unit)
                        .stream()
                        .filter(targetDeployment -> isAccessible(targetDeployment.getKey(), battle.map()))
                        .flatMap(targetDeployment -> Stream.of(targetDeployment.getKey().up(), targetDeployment.getKey().down(), targetDeployment.getKey().left(), targetDeployment.getKey().right()))
                        .filter(c -> battle.getCoordinate(c) == '.')
                        .min(Comparator.<Coordinate, Integer>comparing(c -> c.getValue(distances))
                                .thenComparing(BY_Y_THEN_X))
                        .orElse(null);
                if (targetLocation != null) {
                    Coordinate nextPosition = Stream.of(unitFinalPosition.up(), unitFinalPosition.down(), unitFinalPosition.left(), unitFinalPosition.right())
                            .filter(c -> c.inBounds(battle.map()))
                            .filter(c -> battle.getCoordinate(c) == '.')
                            .filter(c -> c.getValue(distances) == 1)
                            .filter(c -> {
                                ArrayList<Coordinate> path = new ArrayList<>();
                                path.add(c);
                                return canAccessTarget(battle, distances, c, targetLocation, path);
                            })
                            .min(BY_Y_THEN_X)
                            .orElse(null);
                    if (nextPosition != null) {
                        roundMoves++;
                        battle.setCoordinate(nextPosition, unit.getValue().unitKind().type());
                        battle.setCoordinate(unit.getKey(), '.');
                        battle.unitsDeployment().remove(unit.getKey());
                        battle.unitsDeployment().put(nextPosition, unit.getValue());
                        unitFinalPosition = nextPosition;
                    }
                }
                Coordinate unitFinalPositionCopy = unitFinalPosition;
                target = getTargets(battle, unit)
                        .stream()
                        .filter(targetDeployment -> isInRange(unitFinalPositionCopy, targetDeployment))
                        .min(Comparator.<Map.Entry<Coordinate, Unit>, Integer>comparing(c -> c.getValue().hp())
                                .thenComparing(Map.Entry::getKey, BY_Y_THEN_X))
                        .orElse(null);
            }
            if (target != null) {
                unit.getValue().storeAttack();
                roundDamage += target.getValue().damage(unit.getValue().attack());
                if (target.getValue().hp() <= 0) {
                    defeatedUnits.add(target.getValue());
                    Coordinate targetLocation = target.getKey();
                    target.getValue().enemiesHpAndMomentOfDefeat(
                            getTargets(battle, target)
                                    .stream()
                                    .filter(e -> e.getKey().manhattanDistance(targetLocation) == 1)
                                    .mapToInt(e -> e.getValue().hp())
                                    .sum()
                    );
                    battle.unitsDeployment().remove(target.getKey());
                    battle.setCoordinate(target.getKey(), '.');
                    unitsInMoveOrder.remove(target);
                }
            }
            if (getTargets(battle, unit).isEmpty() && !unitsInMoveOrder.isEmpty()) {
                return new RoundResult(roundMoves, roundDamage, false, battle.sumHpOfRemainingUnits(), battle.goblins(), battle.elves(), defeatedUnits);
            }
        }
        return new RoundResult(roundMoves, roundDamage, true, battle.sumHpOfRemainingUnits(), battle.goblins(), battle.elves(), defeatedUnits);
    }

    private boolean canAccessTarget(Battle battle, int[][] distances, Coordinate source, Coordinate target, List<Coordinate> path) {
        if (source.equals(target)) {
            return true;
        }
        for (Coordinate c : List.of(source.up(), source.down(), source.left(), source.right())) {
            if (!c.inBounds(battle.map)) continue;
            if (battle.getCoordinate(c) != '.' && !c.equals(target)) continue;
            if (c.getValue(distances) != source.getValue(distances) + 1) continue;
            if (c.getValue(distances) > target.getValue(distances)) continue;
            path.add(c);
            if (canAccessTarget(battle, distances, c, target, path)) {
                return true;
            }
            path.remove(c);
        }
        return false;
    }

    private record RoundResult(int roundMoves, int roundDamage, boolean roundCompleted, int hpOfRemainingUnits,
                               int goblins, int elves, List<Unit> defeatedUnits) {
    }

    private int[][] distances(Battle battle, Map.Entry<Coordinate, Unit> unit) {
        int[][] distances = new int[battle.map.length][battle.map[0].length];
        for (int[] distance : distances) {
            Arrays.fill(distance, Integer.MAX_VALUE);
        }
        unit.getKey().setValue(distances, 0);
        List<Coordinate> priorityQueue = new ArrayList<>();
        for (int y = 0; y < distances.length; y++) {
            for (int x = 0; x < distances[0].length; x++) {
                priorityQueue.add(new Coordinate(x, y));
            }
        }
        priorityQueue.sort(Comparator.comparing(c -> c.getValue(distances)));
        while (!priorityQueue.isEmpty()) {
            Coordinate coordinate = priorityQueue.removeFirst();
            if (coordinate.getValue(distances) < Integer.MAX_VALUE && (battle.getCoordinate(coordinate) == '.' || coordinate.equals(unit.getKey()))) {
                Stream.of(coordinate.up(), coordinate.down(), coordinate.left(), coordinate.right())
                        .filter(c -> c.inBounds(battle.map()))
                        .filter(c -> battle.getCoordinate(c) != '#')
                        .forEach(c -> {
                            if (c.getValue(distances) > coordinate.getValue(distances) + 1) {
                                c.setValue(distances, coordinate.getValue(distances) + 1);
                            }
                        });
            }
            priorityQueue.sort(Comparator.comparing(c -> c.getValue(distances)));
        }
        return distances;
    }

    private void print(Battle battle) {
        char[][] map = battle.map();
        for (int y = 0; y < map.length; y++) {
            char[] row = map[y];
            System.out.print((y > 9 ? y / 10 + "" : "0") + (y % 10) + "  ");
            StringBuilder additionalLineReport = new StringBuilder();
            for (int x = 0; x < row.length; x++) {
                char c = row[x];
                System.out.print(c);
                if (c == 'G' || c == 'E')
                    additionalLineReport.append((additionalLineReport.isEmpty()) ? "" : ", ").append(String.format("%s(%d)", c, battle.unitsDeployment().get(new Coordinate(x, y)).hp()));
            }
            System.out.println(additionalLineReport.isEmpty() ? "   " : ("   " + additionalLineReport));
        }
    }

    private void print(int[][] distances, List<List<Coordinate>> path) {
        for (int y = 0; y < distances.length; y++) {
            int[] distance = distances[y];
            for (int x = 0; x < distance.length; x++) {
                int d = distance[x];
                String pathLetter = "";
                for (int i = 0; i < path.size(); i++) {
                    if (path.get(i).contains(new Coordinate(x, y))) {
                        pathLetter = String.valueOf((char) ('A' + i));
                        break;
                    }
                }
                String toPrint = pathLetter.isEmpty() ? (d == Integer.MAX_VALUE ? "x" : (d > 9 ? "" : "0") + d) : pathLetter;
                System.out.print(toPrint + "\t");
            }
            System.out.println();
        }
    }

    private boolean isInRange(Coordinate location, Map.Entry<Coordinate, Unit> target) {
        return location.manhattanDistance(target.getKey()) == 1;
    }

    private boolean isAccessible(Coordinate targetLocation, char[][] map) {
        return Stream.of(targetLocation.left(), targetLocation.right(), targetLocation.up(), targetLocation.down())
                .filter(c -> c.inBounds(map))
                .anyMatch(c -> map[c.y()][c.x()] == '.');
    }

    private List<Map.Entry<Coordinate, Unit>> getTargets(Battle battle, Map.Entry<Coordinate, Unit> unit) {
        return battle.unitsDeployment()
                .entrySet()
                .stream()
                .filter(unitDeployment -> unitDeployment.getValue().unitKind() != unit.getValue().unitKind())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public record Battle(char[][] map, Map<Coordinate, Unit> unitsDeployment) {

        private Battle copy(Map<Coordinate, Unit> newDeployments) {
            char[][] map = new char[this.map.length][this.map()[0].length];
            for (int i = 0; i < this.map().length; i++) {
                char[] chars = this.map()[i];
                System.arraycopy(chars, 0, map[i], 0, chars.length);
            }
            if (newDeployments == null) {
                newDeployments = new HashMap<>();
                for (Map.Entry<Coordinate, Unit> entry : this.unitsDeployment().entrySet()) {
                    Coordinate c = entry.getKey();
                    Unit u = entry.getValue();
                    newDeployments.put(c, new Unit(u.unitKind(), u.attack(), u.hp()));
                }
            }
            return new Battle(map, newDeployments);
        }

        private Battle copyWithElvesEnhancement(int attackAdjustement) {
            Map<Coordinate, Unit> newDeployments = new HashMap<>();
            for (Map.Entry<Coordinate, Unit> entry : this.unitsDeployment().entrySet()) {
                Coordinate key = entry.getKey();
                Unit value = entry.getValue();
                if (value.unitKind() == UnitKind.GOBLIN) {
                    newDeployments.put(key, new Unit(value.unitKind(), value.attack(), value.hp()));
                } else {
                    newDeployments.put(key, new Unit(value.unitKind(), value.attack() + attackAdjustement, 200));
                }
            }
            return this.copy(newDeployments);
        }

        public char getCoordinate(Coordinate coordinate) {
            return this.map()[coordinate.y][coordinate.x];
        }

        public char setCoordinate(Coordinate coordinate, char value) {
            return this.map()[coordinate.y][coordinate.x] = value;
        }

        public static Battle parse(List<String> lines) {
            char[][] map = new char[lines.size()][lines.getFirst().length()];
            Map<Day15.Coordinate, Day15.Unit> unitsDeployment = new HashMap<>();
            for (int rowId = 0; rowId < lines.size(); rowId++) {
                char[] row = lines.get(rowId).toCharArray();
                for (int columnId = 0; columnId < row.length; columnId++) {
                    map[rowId][columnId] = row[columnId];
                    switch (row[columnId]) {
                        case 'E' -> unitsDeployment.put(
                                new Day15.Coordinate(columnId, rowId),
                                new Day15.Unit(Day15.UnitKind.ELF, 3, 200));
                        case 'G' -> unitsDeployment.put(
                                new Day15.Coordinate(columnId, rowId),
                                new Day15.Unit(Day15.UnitKind.GOBLIN, 3, 200));
                    }
                }
            }
            return new Day15.Battle(map, unitsDeployment);
        }

        public int sumHpOfRemainingUnits() {
            return unitsDeployment.values().stream().mapToInt(Unit::hp).sum();
        }

        public int goblins() {
            return (int) unitsDeployment().values().stream().filter(u -> u.unitKind() == UnitKind.GOBLIN).count();
        }

        public int elves() {
            return (int) unitsDeployment().values().stream().filter(u -> u.unitKind() == UnitKind.ELF).count();
        }
    }

    public record Coordinate(int x, int y) {
        public int manhattanDistance(Coordinate other) {
            return Math.abs(x - other.x()) + Math.abs(y - other.y());
        }

        public Coordinate up() {
            return new Coordinate(x, y - 1);
        }

        public Coordinate down() {
            return new Coordinate(x, y + 1);
        }

        public Coordinate left() {
            return new Coordinate(x - 1, y);
        }

        public Coordinate right() {
            return new Coordinate(x + 1, y);
        }

        public boolean inBounds(char[][] map) {
            return y >= 0 && y < map.length && x >= 0 && x < map[0].length;
        }

        int getValue(int[][] map) {
            return map[y][x];
        }

        void setValue(int[][] map, int value) {
            map[y][x] = value;
        }

        @Override
        public @NonNull String toString() {
            return "(" + y + ", " + x + ')';
        }
    }

    public static final class Unit {
        private final UnitKind unitKind;
        private final int attack;
        private int hp;
        private int attacks;
        private int enemiesHpAndMomentOfDefeat;

        public Unit(UnitKind unitKind, int attack, int hp) {
            this.unitKind = unitKind;
            this.attack = attack;
            this.hp = hp;
            this.attacks = 0;
            this.enemiesHpAndMomentOfDefeat = 0;
        }

        public UnitKind unitKind() {
            return unitKind;
        }

        public int attack() {
            return attack;
        }

        public int hp() {
            return hp;
        }

        public int damage(int damage) {
            hp -= damage;
            if (hp < 0)
                return damage + hp;
            return damage;
        }

        public void storeAttack() {
            attacks++;
        }

        public void enemiesHpAndMomentOfDefeat(int enemiesHpAndMomentOfDefeat) {
            this.enemiesHpAndMomentOfDefeat = enemiesHpAndMomentOfDefeat;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Unit) obj;
            return Objects.equals(this.unitKind, that.unitKind) &&
                    this.hp == that.hp &&
                    this.attack == that.attack;
        }

        @Override
        public int hashCode() {
            return Objects.hash(unitKind, hp, attack);
        }

        @Override
        public String toString() {
            return "Unit[" +
                    "unitKind=" + unitKind + ", " +
                    "hp=" + hp + ", " +
                    "attack=" + attack + ']';
        }


    }

    public enum UnitKind {
        ELF('E'), GOBLIN('G');
        private final char type;

        UnitKind(char type) {
            this.type = type;
        }

        public char type() {
            return type;
        }
    }

    public static class Distance {
        Coordinate coordinate;
        int distance;
    }
}
