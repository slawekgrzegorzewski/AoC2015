package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;
import com.google.common.base.Function;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;
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
        Map<Coordinate, List<Nanobot>> result = new HashMap<>();
        Map<Cube, List<Nanobot>> cubes = new HashMap<>();
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE, minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
        for (Nanobot nanobot : nanobots) {
            if (nanobot.coordinate().x() - nanobot.radius() < minX) minX = nanobot.coordinate().x() - nanobot.radius();
            if (nanobot.coordinate().x() + nanobot.radius() > maxX) maxX = nanobot.coordinate().x() + nanobot.radius();
            if (nanobot.coordinate().y() - nanobot.radius() < minY) minY = nanobot.coordinate().y() - nanobot.radius();
            if (nanobot.coordinate().y() + nanobot.radius() > maxY) maxY = nanobot.coordinate().y() + nanobot.radius();
            if (nanobot.coordinate().z() - nanobot.radius() < minZ) minZ = nanobot.coordinate().z() - nanobot.radius();
            if (nanobot.coordinate().z() + nanobot.radius() > maxZ) maxZ = nanobot.coordinate().z() + nanobot.radius();
        }
        Cube cube = new Cube(new Coordinate(minX, minY, minZ), Math.max(Math.max(maxX - minX, maxY - minY), maxZ - minZ));
        for (Nanobot nanobot : nanobots) {
            if (cube.isInRange(nanobot))
                cubes.computeIfAbsent(cube, k -> new ArrayList<>()).add(nanobot);
        }

        final Map<Cube, List<Nanobot>> newCubes = new HashMap<>();
        while (!cubes.isEmpty()) {
            int maxRange = cubes.values().stream().mapToInt(List::size).max().orElseThrow();
            cubes.forEach((cube1, nanobots) -> {
                if (nanobots.size() < maxRange)
                    return;
                if (cube1.side() == 0) result.put(cube1.corner(), nanobots);
                List<Cube> split = cube1.split();
                for (Cube cube2 : split) {
                    for (Nanobot nanobot : nanobots) {
                        if (cube2.isInRange(nanobot))
                            newCubes.computeIfAbsent(cube2, k -> new ArrayList<>()).add(nanobot);
                    }
                }
            });
            cubes.clear();
            cubes.putAll(newCubes);
            newCubes.clear();
        }
        Map<Coordinate, Integer> result2 = new HashMap<>();
        result.forEach((c, nanobots) -> result2.put(cube.corner(), nanobots.size()));
        int maxRange = result2.values().stream().max(Integer::compare).orElseThrow();
        return result2.entrySet().stream().filter(e -> e.getValue() == maxRange).map(Map.Entry::getKey)
                .mapToInt(c-> c.manhattanDistance(new Coordinate(0, 0, 0)))
                .min()
                .orElseThrow();
    }

    public record Nanobot(Coordinate coordinate, int radius) {
        public static Nanobot parse(String value) {
            String[] parts = value.split(", ");
            return new Nanobot(Coordinate.parse(parts[0]), Integer.parseInt(parts[1].replace("r=", "")));
        }

        public boolean inRange(Coordinate coordinate) {
            return coordinate.manhattanDistance(this.coordinate()) <= this.radius();
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

        Coordinate moveX(int x) {
            return x(this.x + x);
        }

        Coordinate moveY(int y) {
            return y(this.y + y);
        }

        Coordinate moveZ(int z) {
            return z(this.z + z);
        }

        Coordinate x(int x) {
            return new Coordinate(x, y, z);
        }

        Coordinate y(int y) {
            return new Coordinate(x, y, z);
        }

        Coordinate z(int z) {
            return new Coordinate(x, y, z);
        }
    }

    public record Cube(Coordinate corner, int side) {
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
            Map<Coordinate, Integer> cornersDistances = new HashMap<>();
            Map<Integer, List<Coordinate>> distances = new HashMap<>();
            int minDistance = Integer.MAX_VALUE;
            for (Coordinate coordinate : corners) {
                int distance = coordinate.manhattanDistance(nanobot.coordinate());
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

        private boolean checkSide(Nanobot nanobot, List<Coordinate> side, Map<Coordinate, Integer> cornersDistances) {
            Coordinate startPoint = side.stream().min(Comparator.comparing(cornersDistances::get)).orElseThrow();
            Coordinate endPoint = side.stream().max(Comparator.comparing(cornersDistances::get)).orElseThrow();
            Function<Coordinate, Integer> firstCoordinateGetter;
            BiFunction<Coordinate, Integer, Coordinate> firstCoordinateSetter;
            Function<Coordinate, Integer> secondCoordinateGetter;
            BiFunction<Coordinate, Integer, Coordinate> secondCoordinateSetter;
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
            int firstCoordinateFrom = firstCoordinateGetter.apply(startPoint);
            int firstCoordinateTo = firstCoordinateGetter.apply(endPoint);
            int secondCoordinateFrom = secondCoordinateGetter.apply(startPoint);
            int secondCoordinateTo = secondCoordinateGetter.apply(endPoint);


            BinarySearch binarySearch = new BinarySearch(
                    Math.min(firstCoordinateFrom, firstCoordinateTo),
                    Math.max(firstCoordinateFrom, firstCoordinateTo),
                    i -> firstCoordinateSetter.apply(startPoint, i).manhattanDistance(nanobot.coordinate()));
            int indexOfMinRow = binarySearch.getIndexOfMinValue().orElseThrow();
            Coordinate withFirstCoordinate = firstCoordinateSetter.apply(startPoint, indexOfMinRow);
            binarySearch = new BinarySearch(
                    Math.min(secondCoordinateFrom, secondCoordinateTo),
                    Math.max(secondCoordinateFrom, secondCoordinateTo),
                    i -> secondCoordinateSetter.apply(withFirstCoordinate, i).manhattanDistance(nanobot.coordinate()));
            int indexOfMinColumn = binarySearch.getIndexOfMinValue().orElseThrow();
            return nanobot.inRange(secondCoordinateSetter.apply(withFirstCoordinate, indexOfMinColumn));
        }

        private boolean checkRow(Nanobot nanobot,
                                 int secondCoordinateFrom,
                                 int secondCoordinateTo,
                                 Function<Integer, Integer> secondCoordinateMove,
                                 BiFunction<Coordinate, Integer, Coordinate> secondCoordinateSetter,
                                 Coordinate rowFirstPoint,
                                 int rowMinDistance) {
            if (secondCoordinateFrom > secondCoordinateTo) {
                int cache = secondCoordinateFrom;
                secondCoordinateFrom = secondCoordinateTo;
                secondCoordinateTo = cache;
            }
            while (secondCoordinateFrom + 1 < secondCoordinateTo) {
                int newCoordinate = (secondCoordinateFrom + secondCoordinateTo) / 2;
                Coordinate coordinate = secondCoordinateSetter.apply(rowFirstPoint, newCoordinate);
                int distance = coordinate.manhattanDistance(nanobot.coordinate());
                if (distance <= nanobot.radius)
                    return true;
                if (distance > rowMinDistance) {
                    secondCoordinateTo = newCoordinate;
                } else {
                    secondCoordinateFrom = newCoordinate;
                }
            }
            return false;
        }

        private static List<List<Coordinate>> findSidesToCheck(
                Map<Coordinate, Integer> cornersDistances,
                Map<Integer, List<Coordinate>> distances,
                int minDistance) {
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
                Coordinate minOppositeDistance = Stream.of(oppositeX, oppositeY, oppositeZ).min(Comparator.comparingInt(cornersDistances::get)).orElseThrow();
                for (Coordinate opposite : distances.get(cornersDistances.get(minOppositeDistance))) {
                    if (Stream.of(oppositeX, oppositeY, oppositeZ).noneMatch(opposite::equals)) continue;
                    List<Coordinate> side = new ArrayList<>();
                    side.add(corner);
                    side.add(opposite);
                    side.sort(Comparator.<Coordinate, Integer>comparing(Coordinate::x).thenComparing(c -> c.y()).thenComparing(c -> c.z()));
                    if (!sidesToCheck.contains(side))
                        sidesToCheck.add(side);
                }
            }
            return sidesToCheck;
        }

        public List<Cube> split() {
            int splitPoint = side() / 2;
            return List.of(
                    new Cube(corner(), splitPoint),
                    new Cube(corner().moveX(splitPoint), splitPoint),
                    new Cube(corner().moveY(splitPoint), splitPoint),
                    new Cube(corner().moveZ(splitPoint), splitPoint),
                    new Cube(corner().moveX(splitPoint).moveY(splitPoint), splitPoint),
                    new Cube(corner().moveX(splitPoint).moveZ(splitPoint), splitPoint),
                    new Cube(corner().moveY(splitPoint).moveZ(splitPoint), splitPoint),
                    new Cube(corner().moveX(splitPoint).moveY(splitPoint).moveZ(splitPoint), splitPoint)
            );
        }
    }

    public static final class BinarySearch {
        private final int fromIndexInclusive;
        private final int toIndexInclusive;
        private final ToIntFunction<Integer> indexValueGetter;

        public BinarySearch(int fromIndexInclusive, int toIndexInclusive, ToIntFunction<Integer> indexValueGetter) {
            this.fromIndexInclusive = fromIndexInclusive;
            this.toIndexInclusive = toIndexInclusive;
            this.indexValueGetter = indexValueGetter;
        }

        public OptionalInt getIndexOfMinValue() {
            int fromIndex = fromIndexInclusive;
            int toIndex = toIndexInclusive;
            int startValue = indexValueGetter.applyAsInt(fromIndex);
            int endValue = indexValueGetter.applyAsInt(toIndexInclusive);
            boolean indexChanged = true;
            while (indexChanged) {
                int previousFromIndex = fromIndex;
                int previousToIndex = toIndex;
                indexChanged = false;
                int middleIndex = fromIndex + (toIndex - fromIndex) / 2;
                int middleValue = indexValueGetter.applyAsInt(middleIndex);
                int middleValueNext = indexValueGetter.applyAsInt(middleIndex + 1);
                if (middleValueNext > middleValue) {
                    toIndex = middleIndex;
                } else if (middleValueNext < middleValue) {
                    fromIndex = middleIndex;
                }
                indexChanged = previousFromIndex != fromIndex || previousToIndex != toIndex;
            }
            int min = Integer.MAX_VALUE;
            Integer minIndex = null;
            for (int i = fromIndex; i <= toIndex; i++) {
                int value = indexValueGetter.applyAsInt(i);
                if (value < min) {
                    min = value;
                    minIndex = i;
                }
            }
            return minIndex == null ? OptionalInt.empty() : OptionalInt.of(minIndex);
        }
    }
}
