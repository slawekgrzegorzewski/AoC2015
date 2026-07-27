package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;

public class Day23 {
    private final List<Nanobot> nanobots;


    public Day23() throws IOException {
        this.nanobots = Input.day23();
    }

    long part1() {
        Nanobot nanobot = nanobots.stream().max(Comparator.comparing(Nanobot::radius)).orElseThrow();
        return nanobots.stream()
                .mapToInt(n -> n.coordinate().manhattanDistance(nanobot.coordinate()))
                .filter(n -> n <= nanobot.radius())
                .count();
    }

    long part2() {
        Map<Cuboid, List<Nanobot>> intersections = new HashMap<>();
        List<Nanobot> nanobots = new ArrayList<>(this.nanobots);
        for (Nanobot nanobot : nanobots) {
            Cuboid cuboid = Cuboid.of(nanobot);
            intersections.computeIfAbsent(cuboid, _ -> new ArrayList<>()).add(nanobot);
        }
        while (!nanobots.isEmpty()) {
            Map<Cuboid, List<Nanobot>> intersectionCopy = new HashMap<>();
            Nanobot nanobot = nanobots.removeFirst();
            intersections.forEach((intersection, listOfNanobots) -> {
                if (listOfNanobots.contains(nanobot)) intersectionCopy.put(intersection, listOfNanobots);
                else {
                    intersection.findIntersection(Cuboid.of(nanobot))
                            .ifPresentOrElse(
                                    i -> intersectionCopy.computeIfAbsent(i, _ -> new ArrayList<>(listOfNanobots)).add(nanobot),
                                    () -> intersectionCopy.put(intersection, listOfNanobots));
                }
            });
            intersections = intersectionCopy;
        }
        for (Cuboid cuboid : intersections.keySet()) {
            if(intersections.get(cuboid).size() == 1000)
                System.out.println("a");
        }
        return 0L;
    }

    public record Nanobot(Coordinate coordinate, int radius) {
        public static Nanobot parse(String value) {
            String[] parts = value.split(", ");
            return new Nanobot(Coordinate.parse(parts[0]), Integer.parseInt(parts[1].replace("r=", "")));
        }
    }

    public record Coordinate(int x, int y, int z) {
        public static Coordinate parse(String value) {
            String[] parts = value
                    .replace("pos=<", "")
                    .replace(">", "")
                    .split(",");
            return new Coordinate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }

        public int manhattanDistance(Coordinate other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
        }
    }

    public record Cuboid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        public static Cuboid of(Nanobot nanobot) {
            return new Cuboid(
                    nanobot.coordinate().x() - nanobot.radius(),
                    nanobot.coordinate().x() + nanobot.radius(),
                    nanobot.coordinate().y() - nanobot.radius(),
                    nanobot.coordinate().y() + nanobot.radius(),
                    nanobot.coordinate().z() - nanobot.radius(),
                    nanobot.coordinate().z() + nanobot.radius()
            );
        }

        public Optional<Cuboid> findIntersection(Cuboid other) {
            int fromX = Math.max(minX, other.minX);
            int toX = Math.min(maxX, other.maxX);
            int fromY = Math.max(minY, other.minY);
            int toY = Math.min(maxY, other.maxY);
            int fromZ = Math.max(minZ, other.minZ);
            int toZ = Math.min(maxZ, other.maxZ);
            return toX > fromX && toY > fromY && toZ > fromZ
                    ? Optional.of(new Cuboid(minX, maxX, minY, maxY, minZ, maxZ))
                    : Optional.empty();
        }
    }
}
