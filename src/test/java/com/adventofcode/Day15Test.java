package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day15Test {

    static Day15 day15;

    @BeforeEach
    public void init() throws IOException {
        day15 = new Day15();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day15.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day15.part2());
    }
}