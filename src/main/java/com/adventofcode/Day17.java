package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day17 {
    private static final int TARGET_CAPACITY = 150;

    private final List<Integer> containers;

    public Day17() throws IOException {
        containers = Input.day17();
    }

    long part1() {
        return findCombinations().count();
    }

    long part2() {
        return findCombinations().combinations().size();
    }

    Result findCombinations() {
        long count = 0;
        int minNumberOfContainers = Integer.MAX_VALUE;
        List<List<Integer>> minCombinations = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, containers.size()); i++) {
            List<Integer> containersSummingToTargetCapacity = checkCombination(i, this.containers);
            if(containersSummingToTargetCapacity.isEmpty()) continue;
            count++;
            if (minNumberOfContainers > containersSummingToTargetCapacity.size()) {
                minNumberOfContainers = containersSummingToTargetCapacity.size();
                minCombinations.clear();
                minCombinations.add(containersSummingToTargetCapacity);
            } else if (minNumberOfContainers == containersSummingToTargetCapacity.size()) {
                minCombinations.add(containersSummingToTargetCapacity);
            }
        }
        return new Result(count, minCombinations, minNumberOfContainers);
    }

    private List<Integer> checkCombination(int combination, List<Integer> containers) {
        List<Integer> combinationContainers = new ArrayList<>();
        int index = 0;
        int sum = 0;
        while (index < containers.size()) {
            if (combination % 2 == 1) {
                combinationContainers.add(containers.size() - index - 1);
                sum += containers.get(containers.size() - index - 1);
            }
            combination /= 2;
            index++;
        }
        return sum == TARGET_CAPACITY ? combinationContainers : List.of();
    }

    private record Result(long count, List<List<Integer>> combinations, long minCombinationSize) {
    }
}