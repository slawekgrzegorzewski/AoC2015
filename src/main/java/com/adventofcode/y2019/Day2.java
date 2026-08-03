package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;

import java.io.IOException;
import java.util.List;

public class Day2 {
    private final List<Integer> program;


    public Day2() throws IOException {
        this.program = Input.day2();
    }

    long part1() {
        return executeProgram(12, 2);
    }

    long part2() {
        for (int input1 = 0; input1 < 100; input1++) {
            for (int input2 = 0; input2 < 100; input2++) {
                if (executeProgram(input1, input2) == 19690720) {
                    return 100 * input1 + input2;
                }
            }
        }
        throw new IllegalStateException("No solution found");
    }

    private int executeProgram(int input1, int input2) {
        int maxPosition = this.program.stream().mapToInt(i -> i).max().orElseThrow();
        int[] program = new int[Math.max(maxPosition + 1, this.program.size()) + 4];
        for (int i = 0; i < this.program.size(); i++) {
            program[i] = this.program.get(i);
        }
        program[1] = input1;
        program[2] = input2;
        int position = 0;
        while (program[position] != 99) {
            switch (program[position]) {
                case 1:
                    program[program[position + 3]] = program[program[position + 1]] + program[program[position + 2]];
                    position += 4;
                    break;
                case 2:
                    program[program[position + 3]] = program[program[position + 1]] * program[program[position + 2]];
                    position += 4;
                    break;
            }
        }
        return program[0];
    }
}
