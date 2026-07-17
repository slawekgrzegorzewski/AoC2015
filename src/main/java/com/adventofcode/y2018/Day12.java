package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.HashSet;
import java.util.LongSummaryStatistics;
import java.util.Set;

public class Day12 {
    private final PlantsNotes input;

    public Day12() throws IOException {
        this.input = Input.day12();
    }

    long part1() {
        return sumPotNumbersAfterGenerations(20);
    }

    long part2() {
        return sumPotNumbersAfterGenerations(50_000_000_000L);
    }

    private long sumPotNumbersAfterGenerations(long generations) {
        Set<Long> plants = new HashSet<>();
        char[] initialState = input.initialState().toCharArray();
        for (int i = 0; i < initialState.length; i++) {
            if (initialState[i] == '#')
                plants.add((long) i);
        }
        long lastSum = plants.stream().mapToLong(i -> i).sum();
        long lastDiff = Long.MIN_VALUE;
        int diffStableFromGenerations = 0;
        for (long generation = 0; generation < generations; generation++) {
            plants = simulateNextGeneration(plants);
            long sum = plants.stream().mapToLong(i -> i).sum();
            long diff = sum - lastSum;
            if (diff == lastDiff) {
                diffStableFromGenerations++;
            } else {
                diffStableFromGenerations = 0;
            }
            if (diffStableFromGenerations == 20)
                return sum + (generations - generation - 1) * diff;

            lastDiff = diff;
            lastSum = sum;
        }
        return plants.stream().mapToLong(i -> i).sum();
    }

    private Set<Long> simulateNextGeneration(Set<Long> plants) {
        Set<Long> newPlants = new HashSet<>();
        LongSummaryStatistics statistics = plants.stream().mapToLong(l -> l).summaryStatistics();
        long firstPlant = statistics.getMin() - 2;
        int ruleId = 0;
        for (long i = firstPlant - 3; i < firstPlant + 2; i++) {
            ruleId = (ruleId << 1 | (plants.contains(i) ? 1 : 0)) & 0x1f;
        }
        for (long plant = firstPlant; plant <= statistics.getMax() + 2; plant++) {
            ruleId = (ruleId << 1 | (plants.contains(plant + 2) ? 1 : 0)) & 0x1f;
            if (input.rules()[ruleId]) {
                newPlants.add(plant);
            }
        }
        return newPlants;
    }

    public record PlantsNotes(String initialState, boolean[] rules) {
    }
}
