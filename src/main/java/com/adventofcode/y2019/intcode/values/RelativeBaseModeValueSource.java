package com.adventofcode.y2019.intcode.values;

import com.adventofcode.y2019.intcode.commands.Memory;

public class RelativeBaseModeValueSource extends ValueSource {
    private final Memory<Long> memory;
    private final long position;
    private final long relativeBase;

    public RelativeBaseModeValueSource(Memory<Long> memory, long position, long relativeBase) {
        this.memory = memory;
        this.position = position;
        this.relativeBase = relativeBase;
    }

    public long getValue() {
        return memory.get(memory.get(position) + relativeBase);
    }
}
