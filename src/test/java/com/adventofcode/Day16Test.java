package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day16Test {

    static Day16 day16;

    @BeforeEach
    public void init() throws IOException {
        day16 = new Day16();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day16.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day16.part2());
    }
}