package com.adventofcode.y2018;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day5Test {

    static Day5 day5;

    @BeforeAll
    public static void init() throws IOException {
        day5 = new Day5();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(9370L, day5.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(6390L, day5.part2());
    }
}
