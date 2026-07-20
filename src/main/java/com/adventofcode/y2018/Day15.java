package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
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
        int totalMoves = 0;
        int totalDamage = 0;
        int roundsCompleted = 0;
        while (true) {
            RoundResult roundResult = round(battle);
            totalMoves += roundResult.roundMoves();
            totalDamage += roundResult.roundDamage();
            if (!roundResult.roundCompleted()) {
                return (long) roundsCompleted * battle.sumHpOfRemainingUnits();
            }
            roundsCompleted++;
            if (roundResult.roundDamage() + roundResult.roundMoves() == 0)
                break;
        }
        return (long) roundsCompleted * battle.sumHpOfRemainingUnits();
    }

    private RoundResult round(Battle battle) {
        int roundMoves = 0;
        int roundDamage = 0;
        List<Map.Entry<Coordinate, Unit>> unitsInMoveOrder = battle.unitsDeployment()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(BY_Y_THEN_X))
                .collect(Collectors.toCollection(ArrayList::new));
        while (!unitsInMoveOrder.isEmpty()) {
            Map.Entry<Coordinate, Unit> unit = unitsInMoveOrder.removeFirst();
            Coordinate unitFinalPosition = unit.getKey();
            Map.Entry<Coordinate, Unit> target = getTargets(unit)
                    .stream()
                    .filter(targetDeployment -> isInRange(unit.getKey(), targetDeployment))
                    .min(Map.Entry.<Coordinate, Unit>comparingByValue(Comparator.comparing(Unit::hp))
                            .thenComparing(Map.Entry.comparingByKey(BY_Y_THEN_X)))
                    .orElse(null);
            if (target == null) {
                int[][] distances = distances(unit);

                Coordinate targetLocation = getTargets(unit)
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
                                return canAccessTarget(distances, c, targetLocation, path);
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
                target = getTargets(unit)
                        .stream()
                        .filter(targetDeployment -> isInRange(unitFinalPositionCopy, targetDeployment))
                        .min(Comparator.<Map.Entry<Coordinate, Unit>, Integer>comparing(c -> c.getValue().hp())
                                .thenComparing(Map.Entry::getKey, BY_Y_THEN_X))
                        .orElse(null);
            }
            if (target != null) {
                roundDamage += target.getValue().damage(unit.getValue().attack());
                if (target.getValue().hp() <= 0) {
                    battle.unitsDeployment().remove(target.getKey());
                    battle.setCoordinate(target.getKey(), '.');
                    unitsInMoveOrder.remove(target);
                }
            }
            if (getTargets(unit).isEmpty() && !unitsInMoveOrder.isEmpty()) {
                return new RoundResult(roundMoves, roundDamage, false, battle.sumHpOfRemainingUnits(), battle.goblins(), battle.elves());
            }
        }
        return new RoundResult(roundMoves, roundDamage, true, battle.sumHpOfRemainingUnits(), battle.goblins(), battle.elves());
    }

    private boolean canAccessTarget(int[][] distances, Coordinate source, Coordinate target, List<Coordinate> path) {
        if (source.equals(target)) {
            return true;
        }
        for (Coordinate c : List.of(source.up(), source.down(), source.left(), source.right())) {
            if (!c.inBounds(battle.map)) continue;
            if (battle.getCoordinate(c) != '.' && !c.equals(target)) continue;
            if (c.getValue(distances) != source.getValue(distances) + 1) continue;
            if (c.getValue(distances) > target.getValue(distances)) continue;
            path.add(c);
            if (canAccessTarget(distances, c, target, path)) {
                return true;
            }
            path.remove(c);
        }
        return false;
    }

    private record RoundResult(int roundMoves, int roundDamage, boolean roundCompleted, int hpOfRemainingUnits,
                               int goblins, int elves) {
    }

    private int[][] distances(Map.Entry<Coordinate, Unit> unit) {
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

    private List<Map.Entry<Coordinate, Unit>> getTargets(Map.Entry<Coordinate, Unit> unit) {
        return battle.unitsDeployment()
                .entrySet()
                .stream()
                .filter(unitDeployment -> unitDeployment.getValue().unitKind() != unit.getValue().unitKind())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    long part2() {
        return 0L;
    }

    public record Battle(char[][] map, Map<Coordinate, Unit> unitsDeployment) {

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

        public Unit(UnitKind unitKind, int attack, int hp) {
            this.unitKind = unitKind;
            this.attack = attack;
            this.hp = hp;
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
