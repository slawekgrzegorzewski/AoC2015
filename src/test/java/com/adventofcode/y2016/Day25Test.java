package com.adventofcode.y2016;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day25Test {

    static Day25 day25;

    @BeforeAll
    public static void init() throws IOException {
        day25 = new Day25();
    }

    @Test
    void testPart1() {
        assertEquals(158L, day25.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day25.part2());
    }
}
