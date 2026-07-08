package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.IntSummaryStatistics;
import java.util.List;

public class Day2 {
    private final List<List<Integer>> input;

    public Day2() throws IOException {
        this.input = Input.day2();
    }

    long part1() {
        return input.stream()
                .mapToInt(line -> {
                    IntSummaryStatistics statistics = line.stream().mapToInt(Integer::intValue).summaryStatistics();
                    return statistics.getMax() - statistics.getMin();
                })
                .sum();
    }

    long part2() {
        return input.stream()
                .mapToInt(this::sumAllDivisions)
                .sum();
    }

    private int sumAllDivisions(List<Integer> line) {
        int sum = 0;
        for (int i = 0; i < line.size(); i++) {
            int firstValue = line.get(i);
            for (int j = i + 1; j < line.size(); j++) {
                int secondValue = line.get(j);
                if (firstValue % secondValue == 0) {
                    sum += firstValue / secondValue;
                } else if (secondValue % firstValue == 0) {
                    sum += secondValue / firstValue;
                }
            }
        }
        return sum;
    }
}
