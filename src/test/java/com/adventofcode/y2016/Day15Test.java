package com.adventofcode.y2016;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day15Test {

    static Day15 day15;

    @BeforeAll
    public static void init() throws IOException {
        day15 = new Day15();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day15.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day15.part2());
    }
}
