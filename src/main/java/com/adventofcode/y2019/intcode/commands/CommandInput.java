package com.adventofcode.y2019.intcode.commands;

import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

public record CommandInput(Memory<Long> memory,
                           long instructionPointer,
                           long relativeBase,
                           LongSupplier input,
                           LongConsumer output,
                           boolean debug) {
}
