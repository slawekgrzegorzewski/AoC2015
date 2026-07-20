package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
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
//        print(battle);
//        System.out.println();
        int totalMoves = 0;
        int totalDamage = 0;
        int roundsCompleted = 0;
        while (true) {
            List<Map.Entry<Coordinate, Unit>> unitsInMoveOrder = battle.unitsDeployment()
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey(BY_Y_THEN_X))
                    .collect(Collectors.toCollection(ArrayList::new));
            int roundMoves = 0;
            int roundDamage = 0;
            while (!unitsInMoveOrder.isEmpty()) {
                Map.Entry<Coordinate, Unit> unit = unitsInMoveOrder.removeFirst();
                int[][] distances = distances(unit);
                Map.Entry<Coordinate, Unit> target = getTargets(unit)
                        .stream()
                        .filter(targetDeployment -> isInRange(unit.getKey(), targetDeployment) || isAccessible(targetDeployment, battle.map()))
                        .min(Comparator.<Map.Entry<Coordinate, Unit>, Integer>comparing(c -> getValue(distances, c.getKey()))
                                .thenComparing(Map.Entry::getKey, BY_Y_THEN_X))
                        .orElse(null);
                if (target == null) {
                    continue;
                }
                List<List<Coordinate>> paths = getShortestPaths(distances, unit, target);
//            print(distances, List.of());
//            System.out.println();
//            print(distances, paths);
                Coordinate nextPosition = paths
                        .stream()
                        .filter(Predicate.not(List::isEmpty))
                        .map(List::removeLast)
                        .min(BY_Y_THEN_X)
                        .orElse(null);
                if (nextPosition != null) {
                    roundMoves++;
                    battle.setCoordinate(nextPosition, unit.getValue().unitKind().type());
                    battle.setCoordinate(unit.getKey(), '.');
                    battle.unitsDeployment().remove(unit.getKey());
                    battle.unitsDeployment().put(nextPosition, unit.getValue());
                }
//            print(battle.map());
//            System.out.println();

                target = getTargets(unit)
                        .stream()
                        .filter(targetDeployment -> isInRange(nextPosition == null ? unit.getKey() : nextPosition, targetDeployment))
                        .min(Comparator.<Map.Entry<Coordinate, Unit>, Integer>comparing(c -> c.getValue().hp())
                                .thenComparing(Map.Entry::getKey, BY_Y_THEN_X))
                        .orElse(null);
                if (target != null) {
                    roundDamage += target.getValue().damage(unit.getValue().attack());
                    if (target.getValue().hp() <= 0) {
                        battle.unitsDeployment().remove(target.getKey());
                        battle.setCoordinate(target.getKey(), '.');
                        unitsInMoveOrder.remove(target);
                    }
                }
                if (getTargets(unit).isEmpty() && !unitsInMoveOrder.isEmpty()) {
                    int sumOfHP = battle.unitsDeployment().values().stream().mapToInt(Unit::hp).sum();
                    System.out.println("sumOfHP2 = " + sumOfHP);
                    System.out.println("roundsCompleted = " + roundsCompleted);
                    return (long) roundsCompleted * sumOfHP;
                }
            }
            totalMoves += roundMoves;
            totalDamage += roundDamage;
//            print(battle);
//            System.out.println();
            if (roundDamage + roundMoves == 0)
                break;
            roundsCompleted++;
//            System.out.println("After " + roundsCompleted + " round" + (roundsCompleted == 1 ? "" : "s") + ":");
//            System.out.println("total players = " + battle.unitsDeployment().size());
//            System.out.println("total elves = " + battle.unitsDeployment().values().stream().filter(u -> u.unitKind() == ELF).count());
//            System.out.println("total goblins = " + battle.unitsDeployment().values().stream().filter(u -> u.unitKind() == GOBLIN).count());
//            System.out.println();
//            print(battle);
//            System.out.println();
        }
        int sumOfHP = battle.unitsDeployment().values().stream().mapToInt(Unit::hp).sum();
        System.out.println("totalDamage = " + totalDamage);
        System.out.println("totalMoves = " + totalMoves);
        System.out.println("roundsCompleted = " + roundsCompleted);
        System.out.println("sumOfHP = " + sumOfHP);
        return (long) roundsCompleted * sumOfHP;
        //343245
        //345531 too low
        //345580 too low
        //347915 too high
    }

    private List<List<Coordinate>> getShortestPaths(int[][] distances, Map.Entry<Coordinate, Unit> unit, Map.Entry<Coordinate, Unit> target) {
        List<List<Coordinate>> paths = new ArrayList<>();
        List<Coordinate> currentPath = new ArrayList<>();
        backtrackShortestPaths(distances, unit.getKey(), target.getKey(), target.getKey(), currentPath, paths);
        paths.forEach(path -> {
            path.remove(unit.getKey());
            path.remove(target.getKey());
        });
        return paths;
    }

    private void backtrackShortestPaths(int[][] distances, Coordinate source, Coordinate target, Coordinate current, List<Coordinate> path, List<List<Coordinate>> paths) {
        path.add(current);
        if (current.equals(source)) {
            paths.add(path);
            return;
        }
        if (!current.equals(target) && !current.equals(source) && battle.getCoordinate(current) != '.') return;
        List<Coordinate> previousSteps = Stream.of(current.up(), current.right(), current.down(), current.left())
                .filter(c -> c.inBounds(battle.map()))
                .filter(c -> getValue(distances, c) == getValue(distances, current) - 1)
                .toList();
        Function<List<Coordinate>, List<Coordinate>> listProvider = currentPath -> previousSteps.size() == 1
                ? currentPath
                : new ArrayList<>(currentPath);
        previousSteps.forEach(c -> backtrackShortestPaths(distances, source, target, c, listProvider.apply(path), paths));
    }

    private int[][] distances(Map.Entry<Coordinate, Unit> unit) {
        int[][] distances = new int[battle.map.length][battle.map[0].length];
        for (int[] distance : distances) {
            Arrays.fill(distance, Integer.MAX_VALUE);
        }
        setValue(distances, unit.getKey(), 0);
        List<Coordinate> priorityQueue = new ArrayList<>();
        for (int y = 0; y < distances.length; y++) {
            for (int x = 0; x < distances[0].length; x++) {
                priorityQueue.add(new Coordinate(x, y));
            }
        }
        priorityQueue.sort(Comparator.comparing(c -> getValue(distances, c)));
        while (!priorityQueue.isEmpty()) {
            Coordinate coordinate = priorityQueue.removeFirst();
            if (getValue(distances, coordinate) < Integer.MAX_VALUE && (battle.getCoordinate(coordinate) == '.' || coordinate.equals(unit.getKey()))) {
                Stream.of(coordinate.up(), coordinate.down(), coordinate.left(), coordinate.right())
                        .filter(c -> c.inBounds(battle.map()))
                        .filter(c -> battle.getCoordinate(c) != '#')
                        .forEach(c -> {
                            if (getValue(distances, c) > getValue(distances, coordinate) + 1) {
                                setValue(distances, c, getValue(distances, coordinate) + 1);
                            }
                        });
            }
            priorityQueue.sort(Comparator.comparing(c -> getValue(distances, c)));
        }
        return distances;
    }

    private void print(Battle battle) {
        char[][] map = battle.map();
        for (int y = 0; y < map.length; y++) {
            char[] row = map[y];
            StringBuilder additionalLineReport = new StringBuilder();
            for (int x = 0; x < row.length; x++) {
                char c = row[x];
                System.out.print(c);
                if (c == 'G' || c == 'E')
                    additionalLineReport.append((additionalLineReport.isEmpty()) ? "" : ", ").append(String.format("%s(%d)", c, battle.unitsDeployment().get(new Coordinate(x, y)).hp()));
            }
            System.out.println(additionalLineReport.isEmpty() ? "   " : ("   " + additionalLineReport + ""));
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

    int getValue(int[][] map, Coordinate coordinate) {
        return map[coordinate.y][coordinate.x];
    }

    void setValue(int[][] map, Coordinate coordinate, int value) {
        map[coordinate.y][coordinate.x] = value;
    }

    private boolean isInRange(Coordinate location, Map.Entry<Coordinate, Unit> target) {
        return location.manhattanDistance(target.getKey()) == 1;
    }

    private boolean isAccessible(Map.Entry<Coordinate, Unit> target, char[][] map) {
        Coordinate targetLocation = target.getKey();
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
