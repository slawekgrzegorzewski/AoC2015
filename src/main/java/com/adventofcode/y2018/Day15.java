package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 {
    private final static Comparator<Coordinate> BY_Y_THEN_X = Comparator.comparing(Coordinate::y)
            .thenComparing(Coordinate::x);
    private final Battle battle;

    public Day15() throws IOException {
        this.battle = Input.day15();
    }

    long part1() {
        List<Map.Entry<Coordinate, Unit>> unitsInMoveOrder = battle.unitsDeployment()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(BY_Y_THEN_X))
                .collect(Collectors.toCollection(ArrayList::new));
        while (!unitsInMoveOrder.isEmpty()) {
            Map.Entry<Coordinate, Unit> unit = unitsInMoveOrder.removeFirst();
            List<Map.Entry<Coordinate, Unit>> possibleTargets = getTargets(unit);
            possibleTargets.removeIf(targetDeployment ->
                    !isInRange(unit, targetDeployment) && !isAccessible(targetDeployment, battle.map()));
            int[][] distances = new int[battle.map.length][battle.map[0].length];
            for (int[] distance : distances) {
                Arrays.fill(distance, Integer.MAX_VALUE);
            }
            distances[unit.getKey().y()][unit.getKey().x()] = 0;
            PriorityQueue<Coordinate> priorityQueue = new PriorityQueue<>(Comparator.comparing(c -> distances[c.y][c.x]));
            while (!priorityQueue.isEmpty()){
                Coordinate coordinate = priorityQueue.poll();

            }

        }
        return 0L;
    }

    private boolean isInRange(Map.Entry<Coordinate, Unit> unit, Map.Entry<Coordinate, Unit> target) {
        return unit.getKey().manhattanDistance(target.getKey()) == 1;
    }

    private boolean isAccessible(Map.Entry<Coordinate, Unit> target, char[][] map) {
        Coordinate targetLocation = target.getKey();
        return Stream.of(targetLocation.left(), targetLocation.right(), targetLocation.up(), targetLocation.down())
                .filter(Predicate.not(c -> c.inBounds(map)))
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

    public record Unit(UnitKind unitKind) {

    }

    public enum UnitKind {
        ELF, GOBLIN
    }

    public static class Distance {
        Coordinate coordinate;
        int distance;
    }
}
