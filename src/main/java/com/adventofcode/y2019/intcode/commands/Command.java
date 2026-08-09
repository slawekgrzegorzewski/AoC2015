package com.adventofcode.y2019.intcode.commands;

import com.adventofcode.y2019.intcode.values.RelativeBaseModeValueSource;
import com.adventofcode.y2019.intcode.values.ValueSource;
import com.adventofcode.y2019.intcode.values.ValueSourceFactory;

public abstract class Command {
    public abstract CommandResult execute(CommandInput commandInput) throws InterruptedException;

    public long getValue(Memory<Long> memory, long position, long offset, long relativeBase) {
        return new ValueSourceFactory(memory, position).getValueSource(offset, relativeBase).getValue();
    }
    public long getValueForJump(Memory<Long> memory, long position, long offset, long relativeBase) {
        ValueSource valueSource = new ValueSourceFactory(memory, position).getValueSource(offset, relativeBase);
        return valueSource instanceof RelativeBaseModeValueSource ? memory.get(position + offset) + relativeBase : memory.get(position + offset);
    }

    public abstract boolean isEnd();
}