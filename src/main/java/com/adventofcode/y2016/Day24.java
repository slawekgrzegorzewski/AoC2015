package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 {
    private final char[][] map;


    public Day24() throws IOException {
        this.map = Input.day24();
    }

    long part1() {
        return work(false);
    }

    long part2() {
        return work(true);
    }

    private int work(boolean part2) {
        Map<Pair, Integer> distances = new HashMap<>();
        List<Point> pointsToVisit = new ArrayList<>();
        List<Point> targets = new ArrayList<>();
        Point start = null;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                int value = map[i][j] - '0';
                if (value >= 0 && value <= 9) {
                    Point newPoint = new Point(j, i);
                    pointsToVisit.add(newPoint);
                    if (value != 0) targets.add(newPoint);
                    else start = newPoint;
                }
            }
        }
        for (Point point : pointsToVisit) {
            calculateDistances(point, distances, pointsToVisit);
        }
        List<Point[]> permutations = new ArrayList<>();
        generatePermutations(targets.toArray(new Point[0]), 0, permutations);
        Point pointFinal = start;
        return permutations
                .stream()
                .mapToInt(permutation -> {
                    int distance = distances.get(new Pair(permutation[0], pointFinal)) + (part2 ? distances.get(new Pair(pointFinal, permutation[permutation.length - 1])) : 0);
                    for (int i = 1; i < permutation.length; i++) {
                        distance += distances.get(new Pair(permutation[i], permutation[i - 1]));
                    }
                    return distance;
                })
                .min()
                .orElseThrow();
    }

    private void calculateDistances(Point point, Map<Pair, Integer> collector, List<Point> locations) {
        int[][] distances = new int[map.length][map[0].length];
        Arrays.stream(distances).forEach(row -> Arrays.fill(row, Integer.MAX_VALUE));
        distances[point.y][point.x] = 0;
        Set<Point> pointsToVisit = new HashSet<>();
        pointsToVisit.add(point);
        List<Point> allPoints = new ArrayList<>();
        allPoints.add(point);
        while (!pointsToVisit.isEmpty()) {
            Point working = pointsToVisit.stream().min(Comparator.comparing(p -> getDistance(p, distances))).get();
            pointsToVisit.remove(working);
            List<Point> neighbors = getNeighbors(working);
            for (Point neighbor : neighbors) {
                if (getDistance(working, distances) + 1 < getDistance(neighbor, distances)) {
                    distances[neighbor.y][neighbor.x] = getDistance(working, distances) + 1;
                }
                if (!allPoints.contains(neighbor)) {
                    allPoints.add(neighbor);
                    pointsToVisit.add(neighbor);
                }
            }
        }
        for (Point location : locations) {
            if (location.equals(point))
                return;
            collector.put(new Pair(point, location), getDistance(location, distances));
        }
    }

    private List<Point> getNeighbors(Point point) {
        return Stream.of(
                        point.x() > 0 ? new Point(point.x() - 1, point.y()) : null,
                        point.x() < map[0].length - 1 ? new Point(point.x() + 1, point.y()) : null,
                        point.y() > 0 ? new Point(point.x(), point.y() - 1) : null,
                        point.y() < map.length - 1 ? new Point(point.x(), point.y() + 1) : null
                ).filter(Objects::nonNull)
                .filter(p -> getElement(p, map) != '#')
                .collect(Collectors.toList());
    }

    private char getElement(Point p, char[][] map) {
        return map[p.y][p.x];
    }

    private static int getDistance(Point p, int[][] distances) {
        return distances[p.y][p.x];
    }

    static void generatePermutations(Point[] points, int index, List<Point[]> permutations) {
        if (index == points.length) {
            permutations.add(points.clone());
            return;
        }

        for (int i = index; i < points.length; i++) {
            swap(points, index, i);
            generatePermutations(points, index + 1, permutations);
            swap(points, index, i);
        }
    }

    static void swap(Point[] points, int i, int j) {
        Point tmp = points[i];
        points[i] = points[j];
        points[j] = tmp;
    }

    public record Point(int x, int y) {
        int compareTo(Point other) {
            return Comparator.comparing(Point::x).thenComparing(Point::y).compare(this, other);
        }
    }

    public record Pair(Point p1, Point p2) {
        public Pair(Point p1, Point p2) {
            this.p1 = p1.compareTo(p2) > 0 ? p2 : p1;
            this.p2 = this.p1 == p1 ? p2 : p1;
        }
    }

}
