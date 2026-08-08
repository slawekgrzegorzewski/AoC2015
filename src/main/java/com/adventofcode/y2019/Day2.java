package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;

import java.io.IOException;
import java.util.List;

public class Day2 {
    private final List<Integer> program;


    public Day2() throws IOException {
        this.program = Input.day2();
    }

    long part1() throws InterruptedException {
        return executeProgram(12, 2);
    }

    long part2() throws InterruptedException {
        for (int input1 = 0; input1 < 100; input1++) {
            for (int input2 = 0; input2 < 100; input2++) {
                if (executeProgram(input1, input2) == 19690720) {
                    return 100 * input1 + input2;
                }
            }
        }
        throw new IllegalStateException("No solution found");
    }

    private int executeProgram(int input1, int input2) throws InterruptedException {
        int maxPosition = this.program.stream().mapToInt(i -> i).max().orElseThrow();
        int[] program = new int[Math.max(maxPosition + 1, this.program.size()) + 4];
        for (int i = 0; i < this.program.size(); i++) {
            program[i] = this.program.get(i);
        }
        program[1] = input1;
        program[2] = input2;
        return new IntcodeComputer().execute(program, null, null).memory()[0];
    }
}
