package com.adventofcode.y2019.intcode.commands;

import com.adventofcode.y2019.intcode.values.ValueSourceFactory;

public abstract class Command {
    public abstract CommandResult execute(CommandInput commandInput) throws InterruptedException;

    public int getValue(int[] memory, int position, int offset) {
        return new ValueSourceFactory(memory, position).getValueSource(offset).getValue();
    }

    public abstract boolean isEnd();
}