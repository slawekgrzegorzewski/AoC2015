package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Day5 {
    private final List<Integer> program;


    public Day5() throws IOException {
        this.program = Input.day5();
    }

    long part1() throws InterruptedException {
        BlockingQueue<Integer> input = new ArrayBlockingQueue<>(2);
        input.add(1);
        BlockingDeque<Integer> output = new LinkedBlockingDeque<>(10);
        executeProgram(input, output);
        return output.getLast();
    }

    long part2() throws InterruptedException {
        BlockingQueue<Integer> input = new ArrayBlockingQueue<>(2);
        input.add(5);
        BlockingDeque<Integer> output = new LinkedBlockingDeque<>(2);
        executeProgram(input, output);
        return output.getLast();
    }

    private void executeProgram(BlockingQueue<Integer> input, BlockingQueue<Integer> output) throws InterruptedException {
        int maxPosition = this.program.stream().mapToInt(i -> i).max().orElseThrow();
        int[] program = new int[Math.max(maxPosition + 1, this.program.size()) + 4];
        for (int i = 0; i < this.program.size(); i++) {
            program[i] = this.program.get(i);
        }
        new IntcodeComputer().execute(program, input, output);
    }
}
