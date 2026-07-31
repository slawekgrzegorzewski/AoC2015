package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Day23 {
    private final List<Nanobot> nanobots;


    public Day23() throws IOException {
        this.nanobots = Input.day23();
    }

    long part1() {
        Nanobot nanobot = nanobots.stream().max(Comparator.comparing(Nanobot::radius)).orElseThrow();
        return nanobots.stream()
                .map(Nanobot::coordinate)
                .filter(nanobot::inRange)
                .count();
    }

    long part2() {
        PriorityQueue<Cube> cubes = new PriorityQueue<>();
        cubes.add(getMaxCube());
        while (!cubes.isEmpty()) {
            Cube cube = cubes.poll();
            if (cube.side() == 0) return cube.distanceTo(new Coordinate(0, 0, 0));
            cubes.addAll(cube.split().stream().map(c -> c.withBound(nanobots)).toList());
        }
        throw new RuntimeException("No solution found");
    }

    private Cube getMaxCube() {
        long minX = Long.MAX_VALUE, maxX = Long.MIN_VALUE, minY = Long.MAX_VALUE, maxY = Long.MIN_VALUE, minZ = Long.MAX_VALUE, maxZ = Long.MIN_VALUE;
        for (Nanobot nanobot : nanobots) {
            if (nanobot.coordinate().x() - nanobot.radius() < minX) minX = nanobot.coordinate().x() - nanobot.radius();
            if (nanobot.coordinate().x() + nanobot.radius() > maxX) maxX = nanobot.coordinate().x() + nanobot.radius();
            if (nanobot.coordinate().y() - nanobot.radius() < minY) minY = nanobot.coordinate().y() - nanobot.radius();
            if (nanobot.coordinate().y() + nanobot.radius() > maxY) maxY = nanobot.coordinate().y() + nanobot.radius();
            if (nanobot.coordinate().z() - nanobot.radius() < minZ) minZ = nanobot.coordinate().z() - nanobot.radius();
            if (nanobot.coordinate().z() + nanobot.radius() > maxZ) maxZ = nanobot.coordinate().z() + nanobot.radius();
        }
        return new Cube(
                new Coordinate(minX, minY, minZ),
                Long.highestOneBit(Math.max(Math.max(maxX - minX, maxY - minY), maxZ - minZ)) << 1,
                0);
    }

    public record Nanobot(Coordinate coordinate, long radius) {
        public static Nanobot parse(String value) {
            String[] parts = value.split(", ");
            return new Nanobot(Coordinate.parse(parts[0]), Long.parseLong(parts[1].replace("r=", "")));
        }

        public boolean inRange(Coordinate coordinate) {
            return coordinate.manhattanDistance(this.coordinate()) <= this.radius();
        }
    }

    public record Coordinate(long x, long y, long z) {
        public static Coordinate parse(String value) {
            String[] parts = value
                    .replace("pos=<", "")
                    .replace(">", "")
                    .split(",");
            return new Coordinate(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
        }

        public long manhattanDistance(Coordinate other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
        }

        Coordinate moveX(long x) {
            return x(this.x + x);
        }

        Coordinate moveY(long y) {
            return y(this.y + y);
        }

        Coordinate moveZ(long z) {
            return z(this.z + z);
        }

        Coordinate x(long x) {
            return new Coordinate(x, y, z);
        }

        Coordinate y(long y) {
            return new Coordinate(x, y, z);
        }

        Coordinate z(long z) {
            return new Coordinate(x, y, z);
        }
    }

    public record Cube(Coordinate corner, long side, int bound) implements Comparable<Cube> {

        Cube withBound(List<Nanobot> nanobots) {
            int inRange = 0;
            for (Nanobot nanobot : nanobots) {
                if (distanceTo(nanobot.coordinate()) <= nanobot.radius()) {
                    inRange++;
                }
            }
            return new Cube(corner, side, inRange);
        }

        long distanceTo(Day23.Coordinate coordinate) {
            return axisDistance(coordinate.x(), corner().x)
                    + axisDistance(coordinate.y(), corner().y)
                    + axisDistance(coordinate.z(), corner().z);
        }

        private long axisDistance(long value, long from) {
            if (value < from) return from - value;
            long to = from + side;
            return value > to ? value - to : 0;
        }

        public boolean contains(Coordinate coordinate) {
            return coordinate.x() >= corner.x() && coordinate.x() <= corner.x() + side
                    && coordinate.y() >= corner.y() && coordinate.y() <= corner.y() + side
                    && coordinate.z() >= corner.z() && coordinate.z() <= corner.z() + side;
        }

        public List<Cube> split() {
            long newSide = side() / 2;
            long splitPoint = newSide + 1;
            return List.of(
                    new Cube(corner(), newSide, 0),
                    new Cube(corner().moveX(splitPoint), newSide, 0),
                    new Cube(corner().moveY(splitPoint), newSide, 0),
                    new Cube(corner().moveZ(splitPoint), newSide, 0),
                    new Cube(corner().moveX(splitPoint).moveY(splitPoint), newSide, 0),
                    new Cube(corner().moveX(splitPoint).moveZ(splitPoint), newSide, 0),
                    new Cube(corner().moveY(splitPoint).moveZ(splitPoint), newSide, 0),
                    new Cube(corner().moveX(splitPoint).moveY(splitPoint).moveZ(splitPoint), newSide, 0)
            );
        }

        public int compareTo(Cube other) {
            return Comparator.comparingInt(Cube::bound).reversed()
                    .thenComparing(Cube::side)
                    .thenComparing(c -> c.distanceTo(new Coordinate(0, 0, 0)))
                    .compare(this, other);
        }
    }
}
