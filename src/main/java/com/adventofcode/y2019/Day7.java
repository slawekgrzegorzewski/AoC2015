package com.adventofcode.y2019;

import com.adventofcode.Utils;
import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.ProgramResult;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day7 {
    private final List<Integer> amplifierProgram;
    private final Map<Data, Integer> cache = new HashMap<>();


    public Day7() throws IOException {
        this.amplifierProgram = Input.day7();
    }

    long part1() {
        AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);
        Utils.Permutations.iterate(new int[]{0, 1, 2, 3, 4}, 0, permutation -> {
            int inputValue = 0;
            for (int phase : permutation) {
                ArrayDeque<Integer> input = new ArrayDeque<>();
                input.add(phase);
                input.add(inputValue);
                cache.computeIfAbsent(
                        new Data(phase, inputValue),
                        _ -> executeProgram(input).output().getLast());
                inputValue = cache.get(new Data(phase, inputValue));
            }
            if (inputValue > max.get()) {
                max.set(inputValue);
            }
        });
        return max.get();
    }

    long part2() {
        AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);
        Utils.Permutations.iterate(new int[]{5, 6, 7, 8, 9}, 0, permutation -> {
            int inputValue = 0;
            int index = 0;
            while (true) {
                int phase = permutation[index % 5];
                index++;
                ArrayDeque<Integer> input = new ArrayDeque<>();
                input.add(phase);
                input.add(inputValue);
                cache.computeIfAbsent(
                        new Data(phase, inputValue),
                        _ -> {
                            List<Integer> output = executeProgram(input).output();
                            if (output.isEmpty()) return null;
                            return output.getLast();
                        });
                Integer outputValue = cache.get(new Data(phase, inputValue));
                if (outputValue == null) break;
                inputValue = outputValue;
            }
            if (inputValue > max.get()) {
                max.set(inputValue);
            }
        });
        return max.get();
    }

    private ProgramResult executeProgram(Queue<Integer> input) {
        int maxPosition = this.amplifierProgram.stream().mapToInt(i -> i).max().orElseThrow();
        int[] program = new int[Math.max(maxPosition + 1, this.amplifierProgram.size()) + 4];
        for (int i = 0; i < this.amplifierProgram.size(); i++) {
            program[i] = this.amplifierProgram.get(i);
        }
        return new IntcodeComputer().execute(program, input);
    }

    private record Data(int phase, int input) {

    }
}
