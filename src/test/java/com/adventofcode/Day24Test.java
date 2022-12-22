package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day24Test {

    static Day24 day24;

    @BeforeEach
    public void init() throws IOException {
        day24 = new Day24();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day24.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day24.part2());
    }
}