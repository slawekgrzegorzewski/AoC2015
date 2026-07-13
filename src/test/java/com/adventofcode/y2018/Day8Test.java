package com.adventofcode.y2018;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day8Test {

    static Day8 day8;

    @BeforeAll
    public static void init() throws IOException {
        day8 = new Day8();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day8.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day8.part2());
    }
}
