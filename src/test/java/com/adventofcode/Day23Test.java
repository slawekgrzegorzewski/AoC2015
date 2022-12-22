package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day23Test {

    static Day23 day23;

    @BeforeEach
    public void init() throws IOException {
        day23 = new Day23();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day23.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day23.part2());
    }
}