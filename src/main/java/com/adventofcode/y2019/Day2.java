package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;

import java.io.IOException;
import java.util.List;

public class Day2 {
    private final List<Long> program;


    public Day2() throws IOException {
        this.program = Input.day2();
    }

    long part1() throws InterruptedException {
        return executeProgram(12L, 2L);
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

    private long executeProgram(long input1, long input2) throws InterruptedException {
        Memory<Long> memory = new Memory<>(this.program, 0L);
        memory.set(1, input1);
        memory.set(2, input2);
        return new IntcodeComputer(false).execute(memory, null, null).memory().get(0);
    }
}
