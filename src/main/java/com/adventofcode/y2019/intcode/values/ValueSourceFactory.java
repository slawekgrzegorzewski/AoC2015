package com.adventofcode.y2019.intcode.values;

public class ValueSourceFactory {
    private final int[] memory;
    private final int position;

    public ValueSourceFactory(int[] memory, int position) {
        this.memory = memory;
        this.position = position;
    }

    public ValueSource getValueSource(int offset) {
        int mode = memory[position] / 100;
        for (int i = 0; i < offset - 1; i++) {
            mode /= 10;
        }
        mode %= 10;
        if (mode == 0) {
            return new PositionModeValueSource(memory, position + offset);
        } else {
            return new ImmediateModeValueSource(memory, position + offset);
        }
    }
}
