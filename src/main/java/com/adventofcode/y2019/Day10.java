package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day10 {
    private final Set<Coordinate> asteroids;


    public Day10() throws IOException {
        this.asteroids = Input.day10();
    }

    long part1() {
        return asteroids.stream()
                .mapToInt(this::asteroidsInSight)
                .max()
                .orElseThrow();
    }

    long part2() {
        Coordinate base = asteroids.stream()
                .max(Comparator.comparingInt(this::asteroidsInSight))
                .orElseThrow();
        Map<Direction, ArrayList<Coordinate>> asteroidsByDirection = asteroids.stream()
                .filter(Predicate.not(base::equals))
                .collect(Collectors.groupingBy(
                        asteroid -> Direction.between(base, asteroid),
                        Collectors.toCollection(ArrayList::new)));
        ArrayList<Direction> directions = new ArrayList<>(asteroidsByDirection.keySet());
        directions.sort(Direction::compareTo);
        int loop = -1;
        int removed = 0;
        while (true) {
            loop++;
            for (Direction direction : directions) {
                ArrayList<Coordinate> asteroidsInDirection = asteroidsByDirection.get(direction);
                if (asteroidsInDirection.size() > loop) {
                    if (++removed == 200) {
                        asteroidsInDirection.sort(Comparator.comparingInt(base::manhattanDistance));
                        Coordinate coordinate = asteroidsInDirection.get(loop);
                        return coordinate.x() * 100L + coordinate.y();
                    }
                }
            }
        }
    }

    private int asteroidsInSight(Coordinate potentialBase) {
        Set<Direction> uniqueValues = new HashSet<>();
        for (Coordinate asteroid : asteroids) {
            if (!potentialBase.equals(asteroid)) {
                uniqueValues.add(Direction.between(potentialBase, asteroid));
            }
        }
        return uniqueValues.size();
    }

    public record Coordinate(int x, int y) {
        public int manhattanDistance(Coordinate that) {
            return Math.abs(x - that.x()) + Math.abs(y - that.y);
        }
    }

    public record Direction(int dx, int dy, double angle) implements Comparable<Direction> {
        public static Direction between(Coordinate start, Coordinate end) {
            int dx = end.x() - start.x();
            int dy = end.y() - start.y();
            int gcd = gcd(dx, dy);
            return new Direction(dx / gcd, dy / gcd, angle(dx / gcd, dy / gcd));
        }

        private static double angle(int dx, int dy) {
            double angle = Math.atan2(dx, -dy);
            return angle < 0 ? angle + 2 * Math.PI : angle;
        }

        @Override
        public int compareTo(@NonNull Direction that) {
            return Double.compare(this.angle(), that.angle());
        }

        static int gcd(int a, int b) {
            a = Math.abs(a);
            b = Math.abs(b);
            while (b != 0) {
                int remainder = a % b;
                a = b;
                b = remainder;
            }
            return a;
        }
    }
}
