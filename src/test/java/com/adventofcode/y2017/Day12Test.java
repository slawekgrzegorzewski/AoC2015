package com.adventofcode.y2017;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day12Test {

    static Day12 day12;

    @BeforeAll
    public static void init() throws IOException {
        day12 = new Day12();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day12.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day12.part2());
    }
}
