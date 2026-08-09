package com.adventofcode.y2019.intcode.values;

import com.adventofcode.y2019.intcode.commands.Memory;

public class ValueSourceFactory {
    private final Memory<Long> memory;
    private final long position;

    public ValueSourceFactory(Memory<Long> memory, long position) {
        this.memory = memory;
        this.position = position;
    }

    public ValueSource getValueSource(long offset, long relativeBase) {
        long mode = memory.get(position) / 100;
        for (int i = 0; i < offset - 1; i++) {
            mode /= 10;
        }
        mode %= 10;
        return switch (Math.toIntExact(mode)) {
            case 0 -> new PositionModeValueSource(memory, position + offset);
            case 1 -> new ImmediateModeValueSource(memory, position + offset);
            case 2 -> new RelativeBaseModeValueSource(memory, position + offset, relativeBase);
            default -> throw new IllegalArgumentException("Invalid mode: " + mode);
        };
    }
}
