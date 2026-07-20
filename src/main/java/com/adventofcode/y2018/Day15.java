package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
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
        int attackAdjustment = battleResult.defeatedUnits().stream()
                .filter(unit -> unit.unitKind() == UnitKind.ELF)
                .mapToInt(unit -> unit.enemiesHpAndMomentOfDefeat / unit.attacks / 2)
                .max()
                .orElseThrow();
        battleResult = battle(this.battle.copyWithElvesEnhancement(attackAdjustment));
        int[] lowerAndUpperBoundOfAttackAdjustment = findBounds(
                attackAdjustment,
                !noElfKilled(battleResult));
        for (attackAdjustment = lowerAndUpperBoundOfAttackAdjustment[0];
             attackAdjustment <= lowerAndUpperBoundOfAttackAdjustment[1];
             attackAdjustment += 2) {
            battleResult = battle(this.battle.copyWithElvesEnhancement(attackAdjustment));
            if (noElfKilled(battleResult)) {
                BattleResult battleResultPrev = battle(this.battle.copyWithElvesEnhancement(attackAdjustment - 1));
                if (noElfKilled(battleResultPrev)) {
                    return (long) battleResultPrev.battle().sumHpOfRemainingUnits() * battleResultPrev.completedRounds();
                }
                return (long) battleResult.battle().sumHpOfRemainingUnits() * battleResult.completedRounds();
            }
        }
        throw new RuntimeException();
    }

    private int[] findBounds(int currentBound, boolean isLowerBound) {
        int current = currentBound;
        int next = isLowerBound ? current * 2 : current / 2;
        while (true) {
            BattleResult battleResult = battle(this.battle.copyWithElvesEnhancement(next));
            if (isLowerBound == noElfKilled(battleResult)) {
                return new int[]{Math.min(next, current), Math.max(next, current)};
            }
            current = next;
            next = isLowerBound ? current * 2 : current / 2;
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
        List<Unit> defeatedUnits = new ArrayList<>();

        List<Map.Entry<Coordinate, Unit>> unitsInMoveOrder = battle.unitsDeployment()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(BY_Y_THEN_X))
                .collect(Collectors.toCollection(ArrayList::new));

        while (!unitsInMoveOrder.isEmpty()) {
            Map.Entry<Coordinate, Unit> unit = unitsInMoveOrder.removeFirst();

            Optional<Coordinate> movedTo = move(battle, unit.getValue(), unit.getKey());
            if (movedTo.isPresent())
                roundMoves++;

            AttackResult attackResult = attack(
                    battle,
                    unit.getValue(),
                    movedTo.orElseGet(unit::getKey));
            defeatedUnits.addAll(attackResult.defeatedUnits());
            roundDamage += attackResult.damageGiven();

            unitsInMoveOrder.removeIf(u -> attackResult.defeatedUnits().contains(u.getValue()));
            if (allEnemies(battle, unit.getValue().unitKind()).isEmpty() && !unitsInMoveOrder.isEmpty()) {
                return new RoundResult(roundMoves, roundDamage, false, defeatedUnits);
            }
        }
        return new RoundResult(roundMoves, roundDamage, true, defeatedUnits);
    }

    private Optional<Coordinate> move(Battle battle, Unit unit, Coordinate unitPosition) {
        List<Map.Entry<Coordinate, Unit>> allEnemies = allEnemies(battle, unit.unitKind());

        Map.Entry<Coordinate, Unit> enemyInRange = allEnemies
                .stream()
                .filter(targetDeployment -> isInRange(unitPosition, targetDeployment))
                .min(Map.Entry.<Coordinate, Unit>comparingByValue(Comparator.comparing(Unit::hp))
                        .thenComparing(Map.Entry.comparingByKey(BY_Y_THEN_X)))
                .orElse(null);
        if (enemyInRange != null) {
            return Optional.empty();
        }

        int[][] distances = distances(battle, unitPosition, null);
        Coordinate enemyToAttack = allEnemies
                .stream()
                .map(Map.Entry::getKey)
                .filter(enemy -> isAccessible(enemy, battle.map()))
                .flatMap(enemy -> enemy.adjacentCells(battle.map()))
                .filter(enemyVicinity -> battle.getCoordinate(enemyVicinity) == '.')
                .min(Comparator.<Coordinate, Integer>comparing(enemyVicinity -> enemyVicinity.getValue(distances))
                        .thenComparing(BY_Y_THEN_X))
                .orElse(null);
        if (enemyToAttack == null) {
            return Optional.empty();
        }

        Coordinate moveTo = unitPosition.adjacentCells(battle.map())
                .filter(c -> battle.getCoordinate(c) == '.')
                .filter(c -> c.getValue(distances) == 1)
                .sorted(BY_Y_THEN_X)
                .filter(c -> (enemyToAttack.getValue(distances(battle, c, enemyToAttack)) + 1) == enemyToAttack.getValue(distances))
                .findFirst()
                .orElse(null);
        if (moveTo == null) {
            return Optional.empty();
        }
        battle.setCoordinate(moveTo, unit.unitKind().letter());
        battle.setCoordinate(unitPosition, '.');
        battle.unitsDeployment().remove(unitPosition);
        battle.unitsDeployment().put(moveTo, unit);
        return Optional.of(moveTo);
    }

    private AttackResult attack(Battle battle, Unit unit, Coordinate unitPosition) {
        int damageGiven = 0;
        List<Unit> defeatedUnits = new ArrayList<>();
        Map.Entry<Coordinate, Unit> enemyInRange = allEnemies(battle, unit.unitKind())
                .stream()
                .filter(targetDeployment -> isInRange(unitPosition, targetDeployment))
                .min(Comparator.<Map.Entry<Coordinate, Unit>, Integer>comparing(c -> c.getValue().hp())
                        .thenComparing(Map.Entry::getKey, BY_Y_THEN_X))
                .orElse(null);
        if (enemyInRange == null) {
            return new AttackResult(0, List.of());
        }
        unit.increaseAttacksCounter();
        Unit enemy = enemyInRange.getValue();
        damageGiven += enemy.damage(unit.attack());
        if (enemy.hp() <= 0) {
            defeatedUnits.add(enemy);
            enemy.enemiesHpAndMomentOfDefeat(
                    allEnemies(battle, enemy.unitKind())
                            .stream()
                            .filter(e -> e.getKey().manhattanDistance(enemyInRange.getKey()) == 1)
                            .mapToInt(e -> e.getValue().hp())
                            .sum()
            );
            battle.unitsDeployment().remove(enemyInRange.getKey());
            battle.setCoordinate(enemyInRange.getKey(), '.');
        }
        return new AttackResult(damageGiven, defeatedUnits);
    }

    private int[][] distances(Battle battle, Coordinate startLocation, Coordinate endWhenReachingLocation) {
        int[][] distances = new int[battle.map.length][battle.map[0].length];
        for (int[] distance : distances) {
            Arrays.fill(distance, Integer.MAX_VALUE);
        }
        startLocation.setValue(distances, 0);
        List<Coordinate> priorityQueue = new ArrayList<>();
        priorityQueue.add(startLocation);
        while (!priorityQueue.isEmpty()) {
            Coordinate coordinate = priorityQueue.removeFirst();
            if (coordinate.getValue(distances) < Integer.MAX_VALUE && (battle.getCoordinate(coordinate) == '.' || coordinate.equals(startLocation))) {
                coordinate.adjacentCells(battle.map)
                        .filter(c -> battle.getCoordinate(c) != '#')
                        .forEach(c -> {
                            if (c.getValue(distances) == Integer.MAX_VALUE) {
                                priorityQueue.add(c);
                            }
                            if (c.getValue(distances) > coordinate.getValue(distances) + 1) {
                                c.setValue(distances, coordinate.getValue(distances) + 1);
                            }
                        });
            }
            if (coordinate.equals(endWhenReachingLocation))
                return distances;
            priorityQueue.sort(Comparator.comparing(c -> c.getValue(distances)));
        }
        return distances;
    }

    private boolean isInRange(Coordinate location, Map.Entry<Coordinate, Unit> target) {
        return location.manhattanDistance(target.getKey()) == 1;
    }

    private boolean isAccessible(Coordinate targetLocation, char[][] map) {
        return targetLocation.adjacentCells(map).anyMatch(c -> map[c.y()][c.x()] == '.');
    }

    private List<Map.Entry<Coordinate, Unit>> allEnemies(Battle battle, UnitKind unitKind) {
        return battle.unitsDeployment()
                .entrySet()
                .stream()
                .filter(unitDeployment -> unitDeployment.getValue().unitKind() != unitKind)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public record Battle(char[][] map, Map<Coordinate, Unit> unitsDeployment) {

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

        private Battle copyWithElvesEnhancement(int attackAdjustment) {
            Map<Coordinate, Unit> newDeployments = new HashMap<>();
            for (Map.Entry<Coordinate, Unit> entry : this.unitsDeployment().entrySet()) {
                Coordinate key = entry.getKey();
                Unit value = entry.getValue();
                if (value.unitKind() == UnitKind.GOBLIN) {
                    newDeployments.put(key, new Unit(value.unitKind(), value.attack(), value.hp()));
                } else {
                    newDeployments.put(key, new Unit(value.unitKind(), value.attack() + attackAdjustment, value.hp()));
                }
            }
            return this.copy(newDeployments);
        }

        public char getCoordinate(Coordinate coordinate) {
            return this.map()[coordinate.y][coordinate.x];
        }

        public void setCoordinate(Coordinate coordinate, char value) {
            this.map()[coordinate.y][coordinate.x] = value;
        }

        public int sumHpOfRemainingUnits() {
            return unitsDeployment.values().stream().mapToInt(Unit::hp).sum();
        }
    }

    public record Coordinate(int x, int y) {

        public int manhattanDistance(Coordinate other) {
            return Math.abs(x - other.x()) + Math.abs(y - other.y());
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

        public Stream<Coordinate> adjacentCells(char[][] array) {
            return Stream.of(new Coordinate(x, y - 1),
                            new Coordinate(x, y + 1),
                            new Coordinate(x - 1, y),
                            new Coordinate(x + 1, y))
                    .filter(c -> c.inBounds(array));
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

        public void increaseAttacksCounter() {
            attacks++;
        }

        public void enemiesHpAndMomentOfDefeat(int enemiesHpAndMomentOfDefeat) {
            this.enemiesHpAndMomentOfDefeat = enemiesHpAndMomentOfDefeat;
        }
    }

    public enum UnitKind {
        ELF('E'), GOBLIN('G');
        private final char letter;

        UnitKind(char letter) {
            this.letter = letter;
        }

        public char letter() {
            return letter;
        }
    }

    public record AttackResult(int damageGiven, List<Unit> defeatedUnits) {

    }

    private record RoundResult(int roundMoves, int roundDamage, boolean roundCompleted, List<Unit> defeatedUnits) {
    }
}
