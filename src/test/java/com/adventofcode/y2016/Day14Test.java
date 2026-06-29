package com.adventofcode.y2016;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day14Test {

    static Day14 day14;

    @BeforeAll
    public static void init() throws IOException {
        day14 = new Day14();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day14.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day14.part2());
    }
}
