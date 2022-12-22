package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day25Test {

    static Day25 day25;

    @BeforeEach
    public void init() throws IOException {
        day25 = new Day25();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day25.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day25.part2());
    }
}