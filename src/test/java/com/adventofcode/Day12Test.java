package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day12Test {

    static Day12 day12;

    @BeforeEach
    public void init() throws IOException {
        day12 = new Day12();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day12.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day12.part2());
    }
}