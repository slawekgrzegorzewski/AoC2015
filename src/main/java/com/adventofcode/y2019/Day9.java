package com.adventofcode.y2019;

import com.adventofcode.utils.BlockingDequeWithMemory;
import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Day9 {
    private final List<Long> program;


    public Day9() throws IOException {
        this.program = Input.day9();
    }

    long part1() {
        BlockingDeque<Long> input = new LinkedBlockingDeque<>();
        BlockingDequeWithMemory output = new BlockingDequeWithMemory(Integer.MAX_VALUE);
        input.add(1L);
        try {
            executeProgram(input, output);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return output.getLastAddedElement();
    }

    long part2() {
        BlockingDeque<Long> input = new LinkedBlockingDeque<>();
        BlockingDequeWithMemory output = new BlockingDequeWithMemory(Integer.MAX_VALUE);
        input.add(2L);
        try {
            executeProgram(input, output);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return output.getLastAddedElement();
    }

    private void executeProgram(BlockingQueue<Long> input, BlockingQueue<Long> output) throws InterruptedException {
        Memory<Long> memory = new Memory<>(program, 0L);
        new IntcodeComputer(false).execute(memory, input, output);
    }
}
