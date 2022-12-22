package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day13Test {

    static Day13 day13;

    @BeforeEach
    public void init() throws IOException {
        day13 = new Day13();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day13.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day13.part2());
    }
}