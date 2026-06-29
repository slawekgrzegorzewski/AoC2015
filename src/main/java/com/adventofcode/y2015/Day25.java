package com.adventofcode.y2015;

import com.adventofcode.y2015.input.Input;

import java.io.IOException;

public class Day25 {

    private static final long INITIAL_CODE = 20_151_125L;
    private static final long MULTIPLIER = 252_533L;
    private static final long MODULUS = 33_554_393L;

    private final Input.RowColumn rowColumn;

    public Day25() throws IOException {
        rowColumn = Input.day25();
    }

    long part1() {
        long diagonal = (long) rowColumn.row() + rowColumn.column() - 1;
        long index = diagonal * (diagonal - 1) / 2 + rowColumn.column();
        long code = INITIAL_CODE;
        for (long i = 2; i <= index; i++) {
            code = (code * MULTIPLIER) % MODULUS;
        }
        return code;
    }

    long part2() {
        return 0L;
    }
}