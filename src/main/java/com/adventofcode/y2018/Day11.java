package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public class Day11 {
    private final int serialNumber;


    public Day11() throws IOException {
        this.serialNumber = Input.day11();
    }

    String part1() {
        return findMaxPowerSquareOfSize(initPrefixes(initMap()), 3).toPart1String();
    }

    String part2() {
        return findMaxPowerSquareOfSize(initPrefixes(initMap()), null).toPart2String();
    }

    private int[][] initMap() {
        int[][] map = new int[300][300];
        for (int x = 1; x <= 300; x++) {
            for (int y = 1; y <= 300; y++) {
                map[y - 1][x - 1] = calculatePower(x, y);
            }
        }
        return map;
    }

    private int[][] initPrefixes(int[][] map) {
        int[][] prefixes = new int[map.length][map[0].length];
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (x == 0 && y == 0) {
                    prefixes[0][0] = map[0][0];
                } else if (x == 0) {
                    prefixes[y][0] = map[y][0] + prefixes[y - 1][0];
                } else if (y == 0) {
                    prefixes[0][x] = map[0][x] + prefixes[0][x - 1];
                } else {
                    prefixes[y][x] = map[y][x] + prefixes[y - 1][x] + prefixes[y][x - 1] - prefixes[y - 1][x - 1];
                }
            }
        }
        return prefixes;
    }

    private int calculatePower(int x, int y) {
        int rackId = x + 10;
        int power = rackId * y + serialNumber;
        power *= rackId;
        power = (power / 100) % 10;
        power -= 5;
        return power;
    }

    private static SquareIdentifierWithPower findMaxPowerSquareOfSize(
            int[][] prefixes,
            @Nullable Integer forSizeOnly) {
        int maxPower = Integer.MIN_VALUE;
        int maxX = 0, maxY = 0, sizeOfMax = 0;
        for (int size = forSizeOnly == null ? 1 : 3; size <= (forSizeOnly == null ? 300 : 3); size++) {
            int upperBound = 300 - size;
            for (int y = 0; y <= upperBound; y++) {
                for (int x = 0; x <= upperBound; x++) {
                    int sum = prefixes[y + size - 1][x + size - 1]
                            - (x == 0 ? 0 : prefixes[y + size - 1][x - 1])
                            - (y == 0 ? 0 : prefixes[y - 1][x + size - 1])
                            + ((x == 0 || y == 0) ? 0 : prefixes[y - 1][x - 1]);
                    if (sum > maxPower) {
                        maxPower = sum;
                        maxX = x + 1;
                        maxY = y + 1;
                        sizeOfMax = size;
                    }
                }
            }
        }
        return new SquareIdentifierWithPower(new SquareIdentifier(maxX, maxY, sizeOfMax), maxPower);
    }

    public record SquareIdentifier(int x, int y, int size) {
    }

    public record SquareIdentifierWithPower(SquareIdentifier squareIdentifier, int power) {
        public String toPart1String() {
            return squareIdentifier.x() + "," + squareIdentifier.y();
        }

        public String toPart2String() {
            return squareIdentifier.x() + "," + squareIdentifier.y() + "," + squareIdentifier.size();
        }
    }
}
