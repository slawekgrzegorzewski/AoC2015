package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;

public class Day20 {

    private final int expectedNumberOfPresents;

    public Day20() throws IOException {
        expectedNumberOfPresents = Input.day20();
    }

    long part1() {
        return findLowestHouseNumberWithNumberOfPresentsBiggerOrEqualTo(
                expectedNumberOfPresents,
                simulateElvesVisitingHouses(
                        0,
                        10,
                        divideIntsAndCeil(expectedNumberOfPresents, 10)));
    }

    long part2() {
        return findLowestHouseNumberWithNumberOfPresentsBiggerOrEqualTo(
                expectedNumberOfPresents,
                simulateElvesVisitingHouses(
                        50,
                        11,
                        divideIntsAndCeil(expectedNumberOfPresents, 11)));
    }

    private int divideIntsAndCeil(int dividend, int divisor) {
        return (dividend + divisor - 1) / divisor;
    }

    private static int findLowestHouseNumberWithNumberOfPresentsBiggerOrEqualTo(int expectedNumberOfPresents, int[] presentsSum) {
        for (int i = 0; i < presentsSum.length; i++) {
            if (presentsSum[i] >= expectedNumberOfPresents) {
                return i;
            }
        }
        throw new RuntimeException();
    }

    private static int[] simulateElvesVisitingHouses(
            int elfHousesLimit,
            int elfMultiplier,
            int numberOfHouses) {
        int[] presentsSum = new int[numberOfHouses + 1];
        for (int elfNumber = 1; elfNumber <= numberOfHouses; elfNumber++) {
            int maxHouseNumberToVisit = getMaxHouseNumberToVisit(elfHousesLimit, numberOfHouses, elfNumber);
            int numberOfPresentsGivenByElf = elfNumber * elfMultiplier;
            for (int visitedHouseNumber = elfNumber; visitedHouseNumber <= maxHouseNumberToVisit; visitedHouseNumber += elfNumber) {
                presentsSum[visitedHouseNumber] += numberOfPresentsGivenByElf;
            }
        }
        return presentsSum;
    }

    private static int getMaxHouseNumberToVisit(int elfHousesLimit, int numberOfHouses, int elfNumber) {
        return elfHousesLimit == 0
                ? numberOfHouses
                : Math.min(elfHousesLimit * elfNumber, numberOfHouses);
    }
}