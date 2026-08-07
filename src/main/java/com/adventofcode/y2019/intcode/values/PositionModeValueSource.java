package com.adventofcode.y2019.intcode.values;

public class PositionModeValueSource extends ValueSource {
    private final int[] memory;
    private final int position;

    public PositionModeValueSource(int[] memory, int position) {
        this.memory = memory;
        this.position = position;
    }

    public int getValue() {
        return memory[memory[position]];
    }
}
