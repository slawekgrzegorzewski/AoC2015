package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;

import java.io.IOException;
import java.util.List;

public class Day1 {
    private final List<Long> masses;


    public Day1() throws IOException {
        this.masses = Input.day1();
    }

    long part1() {
        return masses.stream()
                .mapToLong(mass -> mass / 3 - 2)
                .sum();
    }

    long part2() {
        long sumIteratively = masses.stream()
                .mapToLong(this::getFuelNeeded)
                .sum();
        long sumRecursive = masses.stream()
                .mapToLong(this::getFuelNeededRecursively)
                .sum();
        assert sumIteratively == sumRecursive;
        return sumIteratively;
    }

    private long getFuelNeeded(long mass) {
        long previousFuelNeeded = 0;
        long fuelNeeded = mass / 3 - 2;
        while (fuelNeeded > previousFuelNeeded) {
            long diff = fuelNeeded - previousFuelNeeded;
            previousFuelNeeded = fuelNeeded;
            long fuelForDiff = diff / 3 - 2;
            fuelNeeded += fuelForDiff > 0 ? fuelForDiff : 0;
        }
        return fuelNeeded;
    }

    private long getFuelNeededRecursively(long mass) {
        long fuelNeeded = mass / 3 - 2;
        if (fuelNeeded <= 0) {
            return 0;
        }
        return fuelNeeded + getFuelNeededRecursively(fuelNeeded);
    }
}


