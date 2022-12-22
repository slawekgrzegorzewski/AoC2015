package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day22Test {

    static Day22 day22;

    @BeforeEach
    public void init() throws IOException {
        day22 = new Day22();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day22.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day22.part2());
    }
}