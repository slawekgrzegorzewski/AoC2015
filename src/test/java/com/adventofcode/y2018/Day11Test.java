package com.adventofcode.y2018;


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
    void testPart1() {
        assertEquals("20,62", day11.part1());
    }

    @Test
    void testPart2() {
        assertEquals("229,61,16", day11.part2());
    }
}
