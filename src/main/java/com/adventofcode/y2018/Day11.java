package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;

public class Day11 {
    private final int serialNumber;


    public Day11() throws IOException {
        this.serialNumber = Input.day11();
    }

    String part1() {
        int[][] map = initMap();
        int[][] partialSums = copy(map);
        findMaxPowerSquareOfSize(map, partialSums, 2);
        return findMaxPowerSquareOfSize(map, partialSums, 3).toPart1String();
    }

    String part2() {
        int[][] map = initMap();
        int[][] partialSums = copy(map);
        SquareIdentifierWithPower maxPower = new SquareIdentifierWithPower(new SquareIdentifier(0, 0, 0), Integer.MIN_VALUE);
        for (int size = 1; size <= 300; size++) {
            SquareIdentifierWithPower squareIdentifierWithPower = findMaxPowerSquareOfSize(map, partialSums, size);
            if (squareIdentifierWithPower.power() > maxPower.power()) {
                maxPower = squareIdentifierWithPower;
            }
        }
        return maxPower.toPart2String();
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

    private int calculatePower(int x, int y) {
        int rackId = x + 10;
        int power = rackId * y + serialNumber;
        power *= rackId;
        power = (power / 100) % 10;
        power -= 5;
        return power;
    }

    private int[][] copy(int[][] map) {
        int[][] copy = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            System.arraycopy(map[i], 0, copy[i], 0, map[i].length);
        }
        return copy;
    }


    private static SquareIdentifierWithPower findMaxPowerSquareOfSize(int[][] map, int[][] partialSums, int size) {
        int maxPower = Integer.MIN_VALUE;
        int maxX = 0, maxY = 0;
        int upperBound = 300 - size;
        for (int y = 0; y <= upperBound; y++) {
            int[] partialSumRow = partialSums[y];
            int sum = 0;
            for (int x = 0; x <= upperBound; x++) {
                if (x == 0) {
                    for (int xOffset = 0; xOffset < size; xOffset++) {
                        if (size > 1)
                            partialSumRow[xOffset] += map[y + size - 1][xOffset];
                        sum += partialSumRow[xOffset];
                    }
                } else {
                    int newColumn = x + size - 1;
                    if (size > 1)
                        partialSumRow[newColumn] += map[y + size - 1][newColumn];
                    sum -= partialSumRow[x - 1];
                    sum += partialSumRow[newColumn];
                }
                if (sum > maxPower) {
                    maxPower = sum;
                    maxX = x + 1;
                    maxY = y + 1;
                }
            }
        }
        return new SquareIdentifierWithPower(new SquareIdentifier(maxX, maxY, size), maxPower);
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
