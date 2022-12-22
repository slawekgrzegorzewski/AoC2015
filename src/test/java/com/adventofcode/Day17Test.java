package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day17Test {

    static Day17 day17;

    @BeforeEach
    public void init() throws IOException {
        day17 = new Day17();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day17.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day17.part2());
    }
}