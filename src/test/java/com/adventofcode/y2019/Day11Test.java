package com.adventofcode.y2019;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day11Test {

    static Day11 day11;

    @BeforeAll
    public static void init() throws IOException {
        day11 = new Day11();
    }

    @Test
    void testPart1() throws InterruptedException {
        assertEquals(2056L, day11.part1());
    }

    @Test
    void testPart2() throws InterruptedException {
        assertEquals("GLBEPJZP", day11.part2());
    }
}
