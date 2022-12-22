package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day14Test {

    static Day14 day14;

    @BeforeEach
    public void init() throws IOException {
        day14 = new Day14();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day14.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day14.part2());
    }
}