package com.adventofcode.y2018;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day24Test {

    static Day24 day24;

    @BeforeAll
    public static void init() throws IOException {
        day24 = new Day24();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day24.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day24.part2());
    }
}
