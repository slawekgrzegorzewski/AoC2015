package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day18Test {

    static Day18 day18;

    @BeforeEach
    public void init() throws IOException {
        day18 = new Day18();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day18.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day18.part2());
    }
}