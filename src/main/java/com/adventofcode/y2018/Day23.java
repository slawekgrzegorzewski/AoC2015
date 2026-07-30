package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

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
        Cube cube = getMaxCube();
        Map<Cube, Set<Nanobot>> cubes = new HashMap<>();
        cubes.put(cube, new HashSet<>(nanobots));
        long currentSide = cube.side();
        long currentRange = nanobots.size();
        while (currentSide > 0) {
            Map<Cube, Set<Nanobot>> newCubes = new HashMap<>();
            for (var entry : cubes.entrySet()) {
                if (entry.getValue().size() < currentRange) continue;
                if (entry.getKey().side() == 0) continue;
                for (Cube splitCube : entry.getKey().split()) {
                    newCubes.putIfAbsent(splitCube, nanobotsInRange(splitCube, entry.getValue()));
                }
            }
            cubes = newCubes;
            if (currentRange == 1000) {
                Cube shrunkCube = cube.shrink(currentSide / 10);
                cubes.put(shrunkCube, nanobotsInRange(shrunkCube, nanobots));
            }
            retainOnlyMaxRanges(cubes);
            currentSide = cubes.keySet().iterator().next().side();
            currentRange = cubes.values().iterator().next().size();
        }
        return cubes.keySet()
                .stream()
                .mapToLong(c -> c.corner().manhattanDistance(new Coordinate(0, 0, 0)))
                .min()
                .orElseThrow();
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
        return shrinkUntilInFullRange(
                new Cube(
                        new Coordinate(minX, minY, minZ),
                        Long.highestOneBit(Math.max(Math.max(maxX - minX, maxY - minY), maxZ - minZ)) << 1));
    }

    private Cube shrinkUntilInFullRange(Cube cube) {
        while (true) {
            Cube cube1 = cube.shrink(cube.side() / 10);
            if (nanobotsInRange(cube1, nanobots).size() < 1000) return cube;
            cube = cube1;
        }
    }

    private static Set<Nanobot> nanobotsInRange(Cube cube, Collection<Nanobot> nanobots) {
        return nanobots.stream()
                .filter(cube::isInRange)
                .collect(Collectors.toSet());
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

        public static final Comparator<Coordinate> COMPARATOR_BY_X_Y_AND_Z = Comparator.<Coordinate, Long>comparing(Coordinate::x)
                .thenComparing(c -> c.y())
                .thenComparing(c -> c.z());

        Set<Coordinate> corners() {
            Coordinate otherSideCorner = corner.moveZ(side);
            return Set.of(
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

        private Set<Coordinate> oppositeCorners(Coordinate corner) {
            return corners()
                    .stream()
                    .filter(c -> (c.x == corner.x && c.y != corner.y && c.z != corner.z)
                            || (c.x != corner.x && c.y == corner.y && c.z != corner.z)
                            || (c.x != corner.x && c.y != corner.y && c.z == corner.z))
                    .collect(Collectors.toSet());
        }

        public boolean isInRange(Nanobot nanobot) {
            if (contains(nanobot.coordinate()))
                return true;
            if (side == 0) {
                return nanobot.inRange(corner);
            }
            return findSidesToCheck(nanobot).stream()
                    .anyMatch(side -> checkSide(nanobot, side));
        }

        public boolean contains(Coordinate coordinate) {
            return coordinate.x() >= corner.x() && coordinate.x() <= corner.x() + side
                    && coordinate.y() >= corner.y() && coordinate.y() <= corner.y() + side
                    && coordinate.z() >= corner.z() && coordinate.z() <= corner.z() + side;
        }

        private List<List<Coordinate>> findSidesToCheck(Nanobot nanobot) {
            return cornersWithMinDistanceTo(nanobot)
                    .stream()
                    .map(corner -> (List<Coordinate>) new ArrayList<>(
                            List.of(
                                    corner,
                                    oppositeCorners(corner)
                                            .stream()
                                            .min(Comparator.comparingLong(nanobot.coordinate()::manhattanDistance))
                                            .orElseThrow())))
                    .peek(l -> l.sort(COMPARATOR_BY_X_Y_AND_Z))
                    .distinct()
                    .toList();
        }

        private List<Coordinate> cornersWithMinDistanceTo(Nanobot nanobot) {
            long minDistance = Long.MAX_VALUE;
            List<Coordinate> cornersWithMinDistance = new ArrayList<>();
            for (Coordinate coordinate : corners()) {
                long distance = coordinate.manhattanDistance(nanobot.coordinate());
                if (distance < minDistance) {
                    minDistance = distance;
                    cornersWithMinDistance.clear();
                }
                if (distance == minDistance) {
                    cornersWithMinDistance.add(coordinate);
                }
            }
            return cornersWithMinDistance;
        }

        private boolean checkSide(Nanobot nanobot, List<Coordinate> side) {
            Coordinate startPoint = side.stream().min(Comparator.comparing(nanobot.coordinate()::manhattanDistance)).orElseThrow();
            Coordinate endPoint = side.stream().max(Comparator.comparing(nanobot.coordinate()::manhattanDistance)).orElseThrow();
            Function<Coordinate, Long> firstCoordinateGetter;
            BiFunction<Coordinate, Long, Coordinate> firstCoordinateSetter;
            Function<Coordinate, Long> secondCoordinateGetter;
            BiFunction<Coordinate, Long, Coordinate> secondCoordinateSetter;
            if (startPoint.x() == endPoint.x()) {
                firstCoordinateGetter = Coordinate::y;
                firstCoordinateSetter = Coordinate::y;
                secondCoordinateGetter = Coordinate::z;
                secondCoordinateSetter = Coordinate::z;
            } else if (startPoint.y() == endPoint.y()) {
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
            long firstCoordinateFrom = Math.min(firstCoordinateGetter.apply(startPoint), firstCoordinateGetter.apply(endPoint));
            long firstCoordinateTo = Math.max(firstCoordinateGetter.apply(startPoint), firstCoordinateGetter.apply(endPoint));
            long secondCoordinateFrom = Math.min(secondCoordinateGetter.apply(startPoint), secondCoordinateGetter.apply(endPoint));
            long secondCoordinateTo = Math.max(secondCoordinateGetter.apply(startPoint), secondCoordinateGetter.apply(endPoint));

            BinarySearch binarySearch = new BinarySearch(
                    firstCoordinateFrom,
                    firstCoordinateTo,
                    i -> firstCoordinateSetter.apply(startPoint, i).manhattanDistance(nanobot.coordinate()));
            long indexOfMinRow = binarySearch.getIndexOfMinValue().orElseThrow();
            Coordinate withFirstCoordinate = firstCoordinateSetter.apply(startPoint, indexOfMinRow);
            binarySearch = new BinarySearch(
                    secondCoordinateFrom,
                    secondCoordinateTo,
                    i -> secondCoordinateSetter.apply(withFirstCoordinate, i).manhattanDistance(nanobot.coordinate()));
            long indexOfMinColumn = binarySearch.getIndexOfMinValue().orElseThrow();
            return nanobot.inRange(secondCoordinateSetter.apply(withFirstCoordinate, indexOfMinColumn));
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
