package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;

public class Day19 {
    private final int numberOfElves;

    public Day19() throws IOException {
        this.numberOfElves = Input.day19();
    }

    long part1() {
        return 2L * (numberOfElves - Integer.highestOneBit(numberOfElves)) + 1;
    }

    long part2() {
        int p = nearestLowerPowerOf3(numberOfElves);
        if (numberOfElves == p) return p;
        if (numberOfElves < p * 2) return numberOfElves - p;
        return 2L * numberOfElves - 3L * p;
    }

    int nearestLowerPowerOf3(int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }

        int p = 1;
        while (p <= n / 3) {
            p *= 3;
        }
        return p;
    }
}
