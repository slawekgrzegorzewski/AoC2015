package com.adventofcode.y2019.intcode.values;

import com.adventofcode.y2019.intcode.commands.Memory;

public class ImmediateModeValueSource extends ValueSource {
    private final Memory<Long> memory;
    private final long position;

    public ImmediateModeValueSource(Memory<Long> memory, long position) {
        this.memory = memory;
        this.position = position;
    }

    public long getValue() {
        return memory.get(position);
    }
}
