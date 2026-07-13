package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Day1 {
    private final int[] differencies;


    public Day1() throws IOException {
        this.differencies = Input.day1();
    }

    long part1() {
        return Arrays.stream(differencies).sum();
    }

    long part2() {
        Set<Integer> seen = new HashSet<>();
        int runningSum = 0;
        for (int i = 0; i < differencies.length; i = (i + 1) % differencies.length) {
            int difference = differencies[i];
            runningSum += difference;
            if (!seen.add(runningSum)) {
                return runningSum;
            }
        }
        throw new RuntimeException("No duplicate found");
    }
}


