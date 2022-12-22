package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day11Test {

    static Day11 day11;

    @BeforeEach
    public void init() throws IOException {
        day11 = new Day11();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day11.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day11.part2());
    }
}