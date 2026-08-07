package com.adventofcode.y2019.intcode.values;

public class ImmediateModeValueSource extends ValueSource {
    private final int[] memory;
    private final int position;

    public ImmediateModeValueSource(int[] memory, int position) {
        this.memory = memory;
        this.position = position;
    }

    public int getValue() {
        return memory[position];
    }
}
