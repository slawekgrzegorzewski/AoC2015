package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.Function;

public class Day17 {
    private final HashSet<Coordinate> clays;
    private final int minY, maxY;


    public Day17() throws IOException {
        this.clays = Input.day17();
        IntSummaryStatistics yStats = clays.stream().mapToInt(Coordinate::y).summaryStatistics();
        minY = yStats.getMin();
        maxY = yStats.getMax();
    }

    long part1() {
        List<Coordinate> waterStream = new ArrayList<>();
        HashSet<Coordinate> reservoirs = new HashSet<>();
        followStream(waterStream, reservoirs, new Coordinate(500, 0));
        print(waterStream, reservoirs);
        return reservoirs.size() + waterStream.stream().mapToInt(Coordinate::y).filter(y -> y >= minY && y <= maxY).count();
    }

    private void followStream(List<Coordinate> waterStream, HashSet<Coordinate> reservoirs, Coordinate startWith) {
        addToWaterStream(waterStream, reservoirs, startWith);
        Coordinate nextPosition = startWith;
        while (nextPosition.y() <= maxY) {
            print(waterStream, reservoirs);
            nextPosition = nextPosition.down();
            if (!flowIsBlocked(nextPosition, reservoirs)) {
                addToWaterStream(waterStream, reservoirs, nextPosition);
                continue;
            }
            nextPosition = nextPosition.up();
            Coordinate nextPositionLeft = tryDirection(nextPosition, reservoirs, Coordinate::left);
            Coordinate nextPositionRight = tryDirection(nextPosition, reservoirs, Coordinate::right);
            if (nextPositionLeft.y() == nextPosition.y()) {
                if (nextPositionRight.y() == nextPosition.y()) {
                    for (int x = nextPositionLeft.x(); x <= nextPositionRight.x(); x++) {
                        Coordinate newWater = new Coordinate(x, nextPosition.y());
                        addToReservoirs(waterStream, reservoirs, newWater);
                    }
                    waterStream.remove(nextPosition);
                    nextPosition = nextPosition.up();
                }
            }
            if (nextPositionLeft.y() != nextPosition.y() + 1) {
                for (int x = nextPositionLeft.x(); x <= nextPosition.x(); x++) {
                    addToWaterStream(waterStream, reservoirs, new Coordinate(x, nextPosition.y() + 1));
                }
                followStream(waterStream, reservoirs, nextPositionLeft);
            }
            if (nextPositionRight.y() != nextPosition.y() + 1) {
                for (int x = nextPosition.x(); x <= nextPositionRight.x(); x++) {
                    addToWaterStream(waterStream, reservoirs, new Coordinate(x, nextPosition.y() + 1));
                }
                followStream(waterStream, reservoirs, nextPositionRight);
            }
        }
    }

    private static void addToWaterStream(List<Coordinate> waterStream, HashSet<Coordinate> reservoirs, Coordinate newWaterStream) {
        if (!reservoirs.contains(newWaterStream) && !waterStream.contains(newWaterStream))
            waterStream.add(newWaterStream);
    }

    private static void addToReservoirs(List<Coordinate> waterStream, HashSet<Coordinate> reservoirs, Coordinate newReservoir) {
        reservoirs.add(newReservoir);
        waterStream.remove(newReservoir);
    }

    private void print(List<Coordinate> waterStream, HashSet<Coordinate> reservoirs) {
        IntSummaryStatistics xStats = clays.stream().mapToInt(Coordinate::x).summaryStatistics();
        IntSummaryStatistics yStats = clays.stream().mapToInt(Coordinate::y).summaryStatistics();
        for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
            for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
                Coordinate position = new Coordinate(x, y);
                if (clays.contains(position)) System.out.print("#");
                else if (waterStream.contains(position)) System.out.print("|");
                else if (reservoirs.contains(position)) System.out.print("~");
                else System.out.print(".");
            }
            System.out.println();
        }
        System.out.println();
    }

    private boolean flowIsBlocked(Coordinate position, HashSet<Coordinate> reservoirs) {
        return clays.contains(position) || reservoirs.contains(position);
    }

    private Coordinate tryDirection(Coordinate nextPosition, HashSet<Coordinate> reservoirs, Function<Coordinate, Coordinate> nextProvider) {
        Coordinate next = nextPosition;
        while (true) {
            Coordinate next2 = nextProvider.apply(next);
            if (flowIsBlocked(next2, reservoirs)) {
                return next;
            }
            next = next2;
            Coordinate below = next.down();
            if (!flowIsBlocked(below, reservoirs))
                return below;
        }
    }

    long part2() {
        return 0L;
    }

    public record Coordinate(int x, int y) {
        public static List<Coordinate> parse(String value) {
            String[] split = value.split(", ");
            Range xRange, yRange;
            boolean xFirst = split[0].startsWith("x");
            xRange = Range.parse(split[xFirst ? 0 : 1].replace("x=", ""));
            yRange = Range.parse(split[xFirst ? 1 : 0].replace("y=", ""));
            List<Coordinate> coordinates = new ArrayList<>();
            for (int x = xRange.fromInclusive(); x <= xRange.toInclusive(); x++) {
                for (int y = yRange.fromInclusive(); y <= yRange.toInclusive(); y++) {
                    addToWaterStream(coordinates, new HashSet<>(), new Coordinate(x, y));
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
}
