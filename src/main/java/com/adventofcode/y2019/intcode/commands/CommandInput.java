package com.adventofcode.y2019.intcode.commands;

import java.util.concurrent.BlockingQueue;

public record CommandInput(Memory<Long> memory,
                           long instructionPointer,
                           long relativeBase,
                           BlockingQueue<Long> input,
                           BlockingQueue<Long> output,
                           boolean debug) {
}
