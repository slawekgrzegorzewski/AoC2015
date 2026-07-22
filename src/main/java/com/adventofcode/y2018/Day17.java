package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Day17 {
    private static final Function<Coordinate, Coordinate> LEFT = Coordinate::left;
    private static final Function<Coordinate, Coordinate> RIGHT = Coordinate::right;
    private final HashSet<Coordinate> clays;
    private final int minY, maxY;

    public Day17() throws IOException {
        this.clays = Input.day17();
        IntSummaryStatistics yStats = clays.stream().mapToInt(Coordinate::y).summaryStatistics();
        minY = yStats.getMin();
        maxY = yStats.getMax();
    }

    long part1() {
        CaveState caveState = floodCave();
        return caveState.waterStream().stream().map(Coordinate::y).filter(y -> y <= maxY && y >= minY).count() + caveState.reservoirs().size();
    }

    long part2() {
        return floodCave().reservoirs().size();
    }

    private CaveState floodCave() {
        CaveState caveState = new CaveState(clays, new HashSet<>(), new HashSet<>(), new HashMap<>());
        Coordinate startWith = new Coordinate(500, 0);
        caveState.waterStream().add(startWith);
        followStream(caveState, startWith);
        return caveState;
    }

    private void followStream(CaveState caveState, Coordinate startWith) {
        Coordinate nextPosition = startWith;
        while (nextPosition.y() <= maxY) {
            nextPosition = nextPosition.down();
            if (!caveState.flowIsBlocked(nextPosition)) {
                caveState.waterStream().add(nextPosition);
            } else {
                nextPosition = nextPosition.up();
                break;
            }
        }
        while (nextPosition.y() < maxY) {
            OptionalInt leftEnd = followStreamInDirection(caveState, nextPosition, LEFT);
            OptionalInt rightEnd = followStreamInDirection(caveState, nextPosition, RIGHT);
            if (leftEnd.isPresent() && rightEnd.isPresent()) {
                for (int x = leftEnd.getAsInt(); x <= rightEnd.getAsInt(); x++) {
                    Coordinate newReservoir = new Coordinate(x, nextPosition.y());
                    caveState.reservoirs().add(newReservoir);
                    caveState.waterStream().remove(newReservoir);
                }
                nextPosition = nextPosition.up();
            } else {
                break;
            }
        }
    }

    private OptionalInt followStreamInDirection(CaveState caveState, Coordinate startWith, Function<Coordinate, Coordinate> nextPositionProvider) {
        DirectedStreamCacheKey directedStreamCacheKey = new DirectedStreamCacheKey(startWith, nextPositionProvider);
        OptionalInt result = caveState.directedStreamCache().get(directedStreamCacheKey);
        if (result != null) {
            return result;
        }
        Coordinate currentPosition = startWith;
        while (true) {
            boolean positionBelowBlocked = caveState.flowIsBlocked(currentPosition.down());
            if (!positionBelowBlocked) {
                followStream(caveState, currentPosition);
            }
            positionBelowBlocked = caveState.flowIsBlocked(currentPosition.down());
            if (!positionBelowBlocked) {
                caveState.directedStreamCache().put(directedStreamCacheKey, OptionalInt.empty());
                return OptionalInt.empty();
            }

            Coordinate nextPosition = nextPositionProvider.apply(currentPosition);
            boolean nextPositionBlocked = caveState.flowIsBlocked(nextPosition);
            if (nextPositionBlocked) {
                caveState.directedStreamCache().put(directedStreamCacheKey, OptionalInt.of(currentPosition.x()));
                return OptionalInt.of(currentPosition.x());
            }
            caveState.waterStream().add(nextPosition);
            currentPosition = nextPosition;
        }
    }

    public record Coordinate(int x, int y) {
        public static HashSet<Coordinate> parse(String value) {
            String[] split = value.split(", ");
            Range xRange, yRange;
            boolean xFirst = split[0].startsWith("x");
            xRange = Range.parse(split[xFirst ? 0 : 1].replace("x=", ""));
            yRange = Range.parse(split[xFirst ? 1 : 0].replace("y=", ""));
            HashSet<Coordinate> coordinates = new HashSet<>();
            for (int x = xRange.fromInclusive(); x <= xRange.toInclusive(); x++) {
                for (int y = yRange.fromInclusive(); y <= yRange.toInclusive(); y++) {
                    coordinates.add(new Coordinate(x, y));
                }
            }
            return coordinates;
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public record Range(int fromInclusive, int toInclusive) {
        public static Range parse(String value) {
            String[] parts = value.split("\\.\\.");
            if (parts.length == 1) return ofSingle(Integer.parseInt(parts[0]));
            return new Range(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }

        public static Range ofSingle(int value) {
            return new Range(value, value);
        }
    }

    public record CaveState(HashSet<Coordinate> clays,
                            HashSet<Coordinate> waterStream,
                            HashSet<Coordinate> reservoirs,
                            HashMap<DirectedStreamCacheKey, OptionalInt> directedStreamCache) {

        public boolean flowIsBlocked(Coordinate position) {
            return clays().contains(position) || reservoirs().contains(position);
        }
    }

    public record DirectedStreamCacheKey(Coordinate coordinate,
                                         Function<Coordinate, Coordinate> nextLocationProvider) {
    }

}
