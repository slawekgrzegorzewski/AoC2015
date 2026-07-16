package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;

public class Day11 {
    private final int serialNumber;


    public Day11() throws IOException {
        this.serialNumber = Input.day11();
    }

    private void print(int[][] ints) {
        for (int[] ints1 : ints) {
            for (int anInt : ints1) {
                System.out.print(anInt + "\t");
            }
            System.out.println();
        }
    }

    String part1() {
        SquareIdentifierWithPower squareIdentifierWithPower = findMaxPowerSquareOfSize(initMap(), 3);
        return squareIdentifierWithPower.squareIdentifier().x() + "," + squareIdentifierWithPower.squareIdentifier().y();
    }

    String part2() {
        int[][] map = initMap();
        SquareIdentifierWithPower maxPower = new SquareIdentifierWithPower(new SquareIdentifier(0, 0, 0), Integer.MIN_VALUE);
        for (int size = 1; size <= 300; size++) {
            SquareIdentifierWithPower squareIdentifierWithPower = findMaxPowerSquareOfSize(map, size);
            if (squareIdentifierWithPower.power() > maxPower.power()) {
                maxPower = squareIdentifierWithPower;
            }
            if (squareIdentifierWithPower.power() < 0) break;
        }
        return maxPower.squareIdentifier().x() + "," + maxPower.squareIdentifier().y() + "," + maxPower.squareIdentifier().size();
    }

    private int[][] initMap() {
        int[][] map = new int[300][300];
        for (int x = 1; x <= 300; x++) {
            for (int y = 1; y <= 300; y++) {
                map[y - 1][x - 1] = (((x + 10) * y + serialNumber) * (x + 10)) / 100 % 10 - 5;
            }
        }
        return map;
    }


    private static SquareIdentifierWithPower findMaxPowerSquareOfSize(int[][] map, int size) {
        int maxPower = Integer.MIN_VALUE;
        int maxX = 0, maxY = 0;
        for (int x = 0; x < 300 - size; x++) {
            for (int y = 0; y < 300 - size; y++) {
                int sum = 0;
                for (int xOffset = 0; xOffset < size; xOffset++)
                    for (int yOffset = 0; yOffset < size; yOffset++)
                        sum += map[y + yOffset][x + xOffset];
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
    }
}
