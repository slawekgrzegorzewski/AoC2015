package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;

public class Day6 {
    private final List<Coordinate> coordinates;


    public Day6() throws IOException {
        this.coordinates = Input.day6();
    }

    long part1() {
        LongSummaryStatistics xStats = coordinates.stream()
                .mapToLong(Coordinate::x)
                .summaryStatistics();
        LongSummaryStatistics yStats = coordinates.stream()
                .mapToLong(Coordinate::y)
                .summaryStatistics();
        Map<Coordinate, List<Coordinate>> coordinatesWithBelongings = new HashMap<>();
        for (int x = Math.toIntExact(xStats.getMin()); x < xStats.getMax(); x++) {
            for (int y = Math.toIntExact(yStats.getMin()); y < yStats.getMax(); y++) {
                Coordinate coordinate = new Coordinate(x, y);
                if (coordinates.contains(coordinate)) continue;
                findClosestCoordinate(coordinate, coordinates)
                        .ifPresent(c -> coordinatesWithBelongings.computeIfAbsent(c, _ -> new ArrayList<>()).add(coordinate));
            }
        }
        int depth = coordinatesWithBelongings.entrySet()
                .stream()
                .mapToInt(e -> e.getValue().stream().mapToInt(c -> c.manhatanDistance(e.getKey())).max().orElseThrow())
                .max()
                .orElseThrow() + 1;
        for (int x = Math.toIntExact(xStats.getMin()) - depth; x <= xStats.getMax() + depth; x++) {
            Coordinate coordinate = new Coordinate(x, Math.toIntExact(yStats.getMin() - depth));
            findClosestCoordinate(coordinate, coordinates)
                    .ifPresent(coordinatesWithBelongings::remove);
            coordinate = new Coordinate(x, Math.toIntExact(yStats.getMax() + depth));
            findClosestCoordinate(coordinate, coordinates)
                    .ifPresent(coordinatesWithBelongings::remove);
        }
        for (int y = Math.toIntExact(yStats.getMin()) - depth; y <= yStats.getMax() + depth; y++) {
            Coordinate coordinate = new Coordinate(Math.toIntExact(xStats.getMin() - depth), y);
            findClosestCoordinate(coordinate, coordinates)
                    .ifPresent(coordinatesWithBelongings::remove);
            coordinate = new Coordinate(Math.toIntExact(xStats.getMax() + depth), y);
            findClosestCoordinate(coordinate, coordinates)
                    .ifPresent(coordinatesWithBelongings::remove);
        }
        return coordinatesWithBelongings.entrySet().stream()
                .max(Comparator.comparing(entry -> entry.getValue().size()))
                .orElseThrow()
                .getValue()
                .size() + 1;
    }

    private Optional<Coordinate> findClosestCoordinate(Coordinate coordinate, List<Coordinate> coordinates) {
        int minDistance = Integer.MAX_VALUE;
        int minDistanceOccurences = 0;
        Coordinate minDistanceCoordinate = null;
        for (Coordinate c : coordinates) {
            int distance = c.manhatanDistance(coordinate);
            if (distance < minDistance) {
                minDistance = distance;
                minDistanceOccurences = 1;
                minDistanceCoordinate = c;
            } else if (distance == minDistance) {
                minDistanceOccurences++;
            }
        }

        if (minDistanceOccurences == 1) {
            assert minDistanceCoordinate != null;
            return Optional.of(minDistanceCoordinate);
        } else {
            return Optional.empty();
        }
    }

    private int sumOfAllDistances(Coordinate coordinate, List<Coordinate> coordinates) {
        int sum = 0;
        for (Coordinate c : coordinates) {
            sum += c.manhatanDistance(coordinate);
        }
        return sum;
    }

    long part2() {
        LongSummaryStatistics xStats = coordinates.stream()
                .mapToLong(Coordinate::x)
                .summaryStatistics();
        LongSummaryStatistics yStats = coordinates.stream()
                .mapToLong(Coordinate::y)
                .summaryStatistics();
        int requiredDistances = 10_000;
        int range = requiredDistances / coordinates.size();
        Map<Coordinate, Integer> sumOfAllDistances = new HashMap<>();
        for (int i = Math.toIntExact(xStats.getMin() - range); i < xStats.getMax() + range; i++) {
            for (int j = Math.toIntExact(yStats.getMin() - range); j < yStats.getMax() + range; j++) {
                Coordinate coordinate = new Coordinate(i, j);
                sumOfAllDistances.put(coordinate, sumOfAllDistances(coordinate, coordinates));
            }
        }
        return sumOfAllDistances.values().stream().filter(d -> d < requiredDistances).count();
    }

    public record Coordinate(int x, int y) {
        public static Coordinate parse(String value) {
            String[] parts = value.split(", ");
            return new Coordinate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }

        public int manhatanDistance(Coordinate coordinate) {
            return Math.abs(x() - coordinate.x()) + Math.abs(y() - coordinate.y());
        }
    }
}
