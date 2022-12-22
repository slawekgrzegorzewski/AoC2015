package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.List;

public class Day2 {

    private final List<Box> input;

    public Day2() throws IOException {
        input = Input.day2();
    }

    long part1() {
        return input.stream().mapToLong(Box::getPaperArea).sum();
    }

    long part2() {
        return input.stream().mapToLong(Box::getRibbonLength).sum();
    }


}


