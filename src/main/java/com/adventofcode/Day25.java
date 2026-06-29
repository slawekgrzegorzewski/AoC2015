package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;

public class Day25 {

    private final Input.RowColumn rowColumn;

    public Day25() throws IOException {
        rowColumn = Input.day25();
    }

    long part1() {
        long numberOfFullDiagonals = rowColumn.row() + rowColumn.column() - 2;
        long elementToFind = (numberOfFullDiagonals * (numberOfFullDiagonals + 1)) / 2 + rowColumn.column();
        long element = 20151125L;
        for (long i = 2; i <= elementToFind; i++) {
            element = (element * 252533L) % 33554393L;
        }
        return element;
    }

    long part2() {
        return 0L;
    }
}