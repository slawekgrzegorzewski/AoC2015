package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;
import com.google.common.base.Function;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

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
        Map<Cube, Set<Nanobot>> cubes = new HashMap<>();
        Cube cube = getMaxCube();
        cubes.put(cube, nanobotsInRange(cube));
        while (!cubes.isEmpty()) {
            long currentSide = cubes.keySet().iterator().next().side();
            if (currentSide == 0)
                break;
            long maxRange = cubes.values().stream().mapToLong(Set::size).max().orElseThrow();
            if (maxRange == 1000) {
                Cube cube1 = cube.shrink(currentSide / 10);
                Set<Nanobot> nanobotsInRange = nanobotsInRange(cube1);
                if (nanobotsInRange.size() < 1000)
                    break;
                cube = cube1;
                cubes.clear();
                cubes.put(cube, nanobotsInRange);
            }
        }
        final Map<Cube, Set<Nanobot>> newCubes = new HashMap<>();
        while (!cubes.isEmpty()) {
            long currentSide = cubes.keySet().iterator().next().side();
            if (currentSide == 0)
                break;
            long maxRange = cubes.values().stream().mapToLong(Set::size).max().orElseThrow();
            if (maxRange == 1000) {
                Cube cube1 = cube.shrink(currentSide / 10);
                newCubes.computeIfAbsent(cube1, k -> new HashSet<>()).addAll(nanobotsInRange(cube1));
                if (newCubes.size() == 1 && newCubes.values().stream().mapToLong(Set::size).max().orElseThrow() == 1000) {
                    cube = cube1;
                } else {
                    a(cubes, maxRange, newCubes);
                }
            } else {
                a(cubes, maxRange, newCubes);
            }
            cubes.clear();
            cubes.putAll(newCubes);
            newCubes.clear();
        }
        long maxRange = cubes.values().stream().mapToLong(Set::size).max().orElseThrow();

        return cubes.entrySet().stream()
                .filter(entry -> entry.getValue().size() == maxRange)
                .map(Map.Entry::getKey)
                .mapToLong(c -> c.corner().manhattanDistance(new Coordinate(0, 0, 0)))
                .min()
                .orElseThrow();
    }

    private Set<Nanobot> nanobotsInRange(Cube cube) {
        Set<Nanobot> nanobotsInRange = new HashSet<>();
        for (Nanobot nanobot : nanobots) {
            if (cube.isInRange(nanobot))
                nanobotsInRange.add(nanobot);
        }
        return nanobotsInRange;
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
        long side = Long.highestOneBit(Math.max(Math.max(maxX - minX, maxY - minY), maxZ - minZ)) << 1;
        return new Cube(new Coordinate(minX, minY, minZ), side);
    }

    private static void a(Map<Cube, Set<Nanobot>> cubes, long maxRange, Map<Cube, Set<Nanobot>> newCubes) {
        cubes.forEach((cube1, nanobots) -> {
            if (nanobots.size() < maxRange || cube1.side() == 0)
                return;
            List<Cube> split = cube1.split();
            for (Cube cube2 : split) {
                newCubes.computeIfAbsent(cube2, k -> new HashSet<>());
                for (Nanobot nanobot : nanobots) {
                    if (cube2.isInRange(nanobot))
                        newCubes.computeIfAbsent(cube2, k -> new HashSet<>()).add(nanobot);
                }
            }
        });
        retainOnlyMaxRanges(newCubes);
    }

    private static void retainOnlyMaxRanges(Map<Cube, Set<Nanobot>> cubes) {
        int maxNumberOfBotsInRange = cubes.values().stream().mapToInt(Set::size).max().orElseThrow();
        cubes.entrySet().removeIf(entry -> entry.getValue().size() < maxNumberOfBotsInRange);
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

    public record Cube(Coordinate corner, long side) {
        List<Coordinate> corners() {
            Coordinate otherSideCorner = corner.moveZ(side);
            return List.of(
                    corner,
                    corner.moveX(side),
                    corner.moveY(side),
                    corner.moveX(side).moveY(side),
                    otherSideCorner,
                    otherSideCorner.moveX(side),
                    otherSideCorner.moveY(side),
                    otherSideCorner.moveX(side).moveY(side)
            );
        }

        public boolean isInRange(Nanobot nanobot) {
            if (contains(nanobot.coordinate()))
                return true;
            if (side == 0) {
                return nanobot.inRange(corner);
            }
            List<Coordinate> corners = corners();
            Map<Coordinate, Long> cornersDistances = new HashMap<>();
            Map<Long, List<Coordinate>> distances = new HashMap<>();
            long minDistance = Long.MAX_VALUE;
            for (Coordinate coordinate : corners) {
                long distance = coordinate.manhattanDistance(nanobot.coordinate());
                if (distance < minDistance) {
                    minDistance = distance;
                }
                cornersDistances.put(coordinate, distance);
                distances.computeIfAbsent(distance, _ -> new ArrayList<>()).add(coordinate);
            }
            if (distances.get(minDistance).stream().anyMatch(nanobot::inRange))
                return true;
            List<List<Coordinate>> sidesToCheck = findSidesToCheck(cornersDistances, distances, minDistance);
            for (List<Coordinate> side : sidesToCheck) {
                if (checkSide(nanobot, side, cornersDistances)) return true;
            }
            return false;
        }

        public boolean contains(Coordinate coordinate) {
            return coordinate.x() >= corner.x() && coordinate.x() <= corner.x() + side
                    && coordinate.y() >= corner.y() && coordinate.y() <= corner.y() + side
                    && coordinate.z() >= corner.z() && coordinate.z() <= corner.z() + side;
        }

        private boolean checkSide(Nanobot nanobot, List<Coordinate> side, Map<Coordinate, Long> cornersDistances) {
            Coordinate startPolong = side.stream().min(Comparator.comparing(cornersDistances::get)).orElseThrow();
            Coordinate endPolong = side.stream().max(Comparator.comparing(cornersDistances::get)).orElseThrow();
            Function<Coordinate, Long> firstCoordinateGetter;
            BiFunction<Coordinate, Long, Coordinate> firstCoordinateSetter;
            Function<Coordinate, Long> secondCoordinateGetter;
            BiFunction<Coordinate, Long, Coordinate> secondCoordinateSetter;
            if (startPolong.x() == endPolong.x()) {
                firstCoordinateGetter = Coordinate::y;
                firstCoordinateSetter = Coordinate::y;
                secondCoordinateGetter = Coordinate::z;
                secondCoordinateSetter = Coordinate::z;
            } else if (startPolong.y() == endPolong.y()) {
                firstCoordinateGetter = Coordinate::x;
                firstCoordinateSetter = Coordinate::x;
                secondCoordinateGetter = Coordinate::z;
                secondCoordinateSetter = Coordinate::z;
            } else {
                firstCoordinateGetter = Coordinate::x;
                firstCoordinateSetter = Coordinate::x;
                secondCoordinateGetter = Coordinate::y;
                secondCoordinateSetter = Coordinate::y;
            }
            long firstCoordinateFrom = firstCoordinateGetter.apply(startPolong);
            long firstCoordinateTo = firstCoordinateGetter.apply(endPolong);
            long secondCoordinateFrom = secondCoordinateGetter.apply(startPolong);
            long secondCoordinateTo = secondCoordinateGetter.apply(endPolong);


            BinarySearch binarySearch = new BinarySearch(
                    Math.min(firstCoordinateFrom, firstCoordinateTo),
                    Math.max(firstCoordinateFrom, firstCoordinateTo),
                    i -> firstCoordinateSetter.apply(startPolong, i).manhattanDistance(nanobot.coordinate()));
            long indexOfMinRow = binarySearch.getIndexOfMinValue().orElseThrow();
            Coordinate withFirstCoordinate = firstCoordinateSetter.apply(startPolong, indexOfMinRow);
            binarySearch = new BinarySearch(
                    Math.min(secondCoordinateFrom, secondCoordinateTo),
                    Math.max(secondCoordinateFrom, secondCoordinateTo),
                    i -> secondCoordinateSetter.apply(withFirstCoordinate, i).manhattanDistance(nanobot.coordinate()));
            long indexOfMinColumn = binarySearch.getIndexOfMinValue().orElseThrow();
            return nanobot.inRange(secondCoordinateSetter.apply(withFirstCoordinate, indexOfMinColumn));
        }

        private static List<List<Coordinate>> findSidesToCheck(
                Map<Coordinate, Long> cornersDistances,
                Map<Long, List<Coordinate>> distances,
                long minDistance) {
            List<List<Coordinate>> sidesToCheck = new ArrayList<>();
            Set<Coordinate> corners = cornersDistances.keySet();
            for (Coordinate corner : distances.get(minDistance)) {
                Coordinate oppositeX = corners.stream()
                        .filter(c -> c.x == corner.x && c.y != corner.y && c.z != corner.z)
                        .findAny()
                        .orElseThrow();
                Coordinate oppositeY = corners.stream()
                        .filter(c -> c.x != corner.x && c.y == corner.y && c.z != corner.z)
                        .findAny()
                        .orElseThrow();
                Coordinate oppositeZ = corners.stream()
                        .filter(c -> c.x != corner.x && c.y != corner.y && c.z == corner.z)
                        .findAny()
                        .orElseThrow();
                Coordinate minOppositeDistance = Stream.of(oppositeX, oppositeY, oppositeZ).min(Comparator.comparingLong(cornersDistances::get)).orElseThrow();
                for (Coordinate opposite : distances.get(cornersDistances.get(minOppositeDistance))) {
                    if (Stream.of(oppositeX, oppositeY, oppositeZ).noneMatch(opposite::equals)) continue;
                    List<Coordinate> side = new ArrayList<>();
                    side.add(corner);
                    side.add(opposite);
                    side.sort(Comparator.<Coordinate, Long>comparing(Coordinate::x).thenComparing(c -> c.y()).thenComparing(c -> c.z()));
                    if (!sidesToCheck.contains(side))
                        sidesToCheck.add(side);
                }
            }
            return sidesToCheck;
        }

        public List<Cube> split() {
            long newSide = side() / 2;
            long splitPolong = newSide + 1;
            return List.of(
                    new Cube(corner(), newSide),
                    new Cube(corner().moveX(splitPolong), newSide),
                    new Cube(corner().moveY(splitPolong), newSide),
                    new Cube(corner().moveZ(splitPolong), newSide),
                    new Cube(corner().moveX(splitPolong).moveY(splitPolong), newSide),
                    new Cube(corner().moveX(splitPolong).moveZ(splitPolong), newSide),
                    new Cube(corner().moveY(splitPolong).moveZ(splitPolong), newSide),
                    new Cube(corner().moveX(splitPolong).moveY(splitPolong).moveZ(splitPolong), newSide)
            );
        }

        public Cube shrink(long size) {
            return new Cube(corner().moveX(size).moveY(size).moveZ(size), side() - size);
        }
    }

    public static final class BinarySearch {
        private final long fromIndexInclusive;
        private final long toIndexInclusive;
        private final ToLongFunction<Long> indexValueGetter;

        public BinarySearch(long fromIndexInclusive, long toIndexInclusive, ToLongFunction<Long> indexValueGetter) {
            this.fromIndexInclusive = fromIndexInclusive;
            this.toIndexInclusive = toIndexInclusive;
            this.indexValueGetter = indexValueGetter;
        }

        public OptionalLong getIndexOfMinValue() {
            long fromIndex = fromIndexInclusive;
            long toIndex = toIndexInclusive;
            boolean indexChanged = true;
            while (indexChanged) {
                long previousFromIndex = fromIndex;
                long previousToIndex = toIndex;
                long middleIndex = fromIndex + (toIndex - fromIndex) / 2;
                long middleValue = indexValueGetter.applyAsLong(middleIndex);
                long middleValueNext = indexValueGetter.applyAsLong(middleIndex + 1);
                if (middleValueNext > middleValue) {
                    toIndex = middleIndex;
                } else if (middleValueNext < middleValue) {
                    fromIndex = middleIndex;
                }
                indexChanged = previousFromIndex != fromIndex || previousToIndex != toIndex;
            }
            long min = Long.MAX_VALUE;
            Long minIndex = null;
            for (long i = fromIndex; i <= toIndex; i++) {
                long value = indexValueGetter.applyAsLong(i);
                if (value < min) {
                    min = value;
                    minIndex = i;
                }
            }
            return minIndex == null ? OptionalLong.empty() : OptionalLong.of(minIndex);
        }
    }
}
