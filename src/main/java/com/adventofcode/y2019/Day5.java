package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Day5 {
    private final List<Long> program;


    public Day5() throws IOException {
        this.program = Input.day5();
    }

    long part1() throws InterruptedException {
        BlockingQueue<Long> input = new ArrayBlockingQueue<>(2);
        input.add(1L);
        BlockingDeque<Long> output = new LinkedBlockingDeque<>(10);
        executeProgram(input, output);
        return output.getLast();
    }

    long part2() throws InterruptedException {
        BlockingQueue<Long> input = new ArrayBlockingQueue<>(2);
        input.add(5L);
        BlockingDeque<Long> output = new LinkedBlockingDeque<>(2);
        executeProgram(input, output);
        return output.getLast();
    }

    private void executeProgram(BlockingQueue<Long> input, BlockingQueue<Long> output) throws InterruptedException {
        Memory<Long> memory = new Memory<>(this.program, 0L);
        new IntcodeComputer(false).execute(memory, input, output);
    }
}
