package com.adventofcode.y2019.intcode.commands;

import java.util.concurrent.BlockingQueue;

public record CommandInput(int[] memory, int instructionPointer,
                           BlockingQueue<Integer> input,
                           BlockingQueue<Integer> output) {
}
