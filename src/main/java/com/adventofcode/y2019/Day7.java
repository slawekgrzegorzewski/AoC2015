package com.adventofcode.y2019;

import com.adventofcode.utils.BlockingDequeWithMemory;
import com.adventofcode.utils.Utils;
import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

public class Day7 {
    private final List<Long> amplifierProgram;

    public Day7() throws IOException {
        this.amplifierProgram = Input.day7();
    }

    long part1() {
        AtomicLong max = new AtomicLong(Long.MIN_VALUE);
        Utils.Permutations.iterate(new int[]{0, 1, 2, 3, 4}, 0, permutation -> {
            long inputValue = 0;
            for (int phase : permutation) {
                BlockingQueue<Long> input = new ArrayBlockingQueue<>(2);
                input.add((long) phase);
                input.add(inputValue);
                BlockingDeque<Long> output = new LinkedBlockingDeque<>(2);
                try {
                    executeProgram(input, output);
                    inputValue = output.getLast();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (inputValue > max.get()) {
                max.set(inputValue);
            }
        });
        return max.get();
    }

    long part2() {
        AtomicLong max = new AtomicLong(Long.MIN_VALUE);
        List<BlockingDequeWithMemory> queues = new ArrayList<>(5);
        List<Thread> threads = new ArrayList<>(5);
        Utils.Permutations.iterate(new int[]{5, 6, 7, 8, 9}, 0, permutation -> {
            queues.clear();
            threads.clear();
            for (int i = 0; i < 5; i++) {
                BlockingDequeWithMemory queue = new BlockingDequeWithMemory(2);
                queue.add((long) permutation[i]);
                queues.add(queue);
                int amplifierIndex = i;
                threads.add(new Thread(() -> {
                    try {
                        executeProgram(queues.get(amplifierIndex), queues.get((amplifierIndex + 1) % 5));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            for (Thread thread : threads) {
                thread.start();
            }
            queues.getFirst().add(0L);
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for amplifier threads", e);
                }
            }
            long finalOutput = queues.getFirst().getLastAddedElement();
            if (finalOutput > max.get()) {
                max.set(finalOutput);
            }
        });
        return max.get();
    }

    private void executeProgram(BlockingQueue<Long> input, BlockingQueue<Long> output) throws InterruptedException {
        Memory<Long> memory = new Memory<>(amplifierProgram, 0L);
        new IntcodeComputer(false).execute(memory, input, output);
    }
}
