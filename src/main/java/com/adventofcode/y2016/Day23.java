package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class Day23 {
    private final List<Day12.Command> compiledCode;


    public Day23() throws IOException {
        this.compiledCode = Input.day23().stream()
                .map(Day12::compileCommand)
                .toList();
    }

    long part1() {
        return 0L;
    }

    long part2() {
        return 0L;
    }
}
