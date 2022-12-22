package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day10Test {

    static Day10 day10;

    @BeforeEach
    public void init() throws IOException {
        day10 = new Day10();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day10.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day10.part2());
    }
}