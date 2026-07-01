package com.adventofcode.y2016;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day10Test {

    static Day10 day10;

    @BeforeAll
    public static void init() throws IOException {
        day10 = new Day10();
    }

    @Test
    void testPart1() {
        assertEquals(98L, day10.part1());
    }

    @Test
    void testPart2() {
        assertEquals(4042L, day10.part2());
    }
}
