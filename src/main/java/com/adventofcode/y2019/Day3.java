package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Day3 {
    private final List<List<String>> wirePaths;
    private final long[] solution;

    public Day3() throws IOException {
        this.wirePaths = Input.day3();
        solution = solve();
    }

    long part1() {
        return solution[0];
    }

    long part2() {
        return solution[1];
    }

    long[] solve() {
        Map<Coordinate, Integer> path1 = getPath(wirePaths.getFirst());
        Map<Coordinate, Integer> path2 = getPath(wirePaths.getLast());
        HashSet<Coordinate> intersections = new HashSet<>(path1.keySet());
        intersections.retainAll(path2.keySet());
        return new long[]{
                intersections.stream()
                        .mapToLong(coordinate -> Math.abs(coordinate.x) + Math.abs(coordinate.y))
                        .filter(distance -> distance > 0)
                        .min()
                        .orElseThrow(),
                intersections.stream()
                        .mapToLong(intersection -> path1.get(intersection) + path2.get(intersection))
                        .filter(distance -> distance > 0)
                        .min()
                        .orElseThrow()};
    }

    private Map<Coordinate, Integer> getPath(List<String> wire) {
        Map<Coordinate, Integer> path1 = new LinkedHashMap<>();
        Coordinate currentPoint = new Coordinate(0, 0);
        int currentDistance = 0;
        for (String move : wire) {
            List<Coordinate> path = trackMove(currentPoint, move);
            for (Coordinate coordinate : path) {
                currentPoint = path.getLast();
                currentDistance++;
                int finalCurrentDistance = currentDistance;
                path1.putIfAbsent(coordinate, finalCurrentDistance);
            }
        }
        return path1;
    }

    public List<Coordinate> trackMove(Coordinate firstPoint, String move) {
        List<Coordinate> path = new ArrayList<>();
        Coordinate currentPoint = firstPoint;
        char direction = move.charAt(0);
        long distance = Integer.parseInt(move.substring(1));
        Function<Coordinate, Coordinate> nextPointSupplier = switch (direction) {
            case 'U' -> coordinate -> new Coordinate(coordinate.x, coordinate.y - 1);
            case 'D' -> coordinate -> new Coordinate(coordinate.x, coordinate.y + 1);
            case 'L' -> coordinate -> new Coordinate(coordinate.x - 1, coordinate.y);
            case 'R' -> coordinate -> new Coordinate(coordinate.x + 1, coordinate.y);
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        };
        for (int i = 0; i < distance; i++) {
            currentPoint = nextPointSupplier.apply(currentPoint);
            path.add(currentPoint);
        }
        return path;
    }

    public record Coordinate(long x, long y) {
    }
}
