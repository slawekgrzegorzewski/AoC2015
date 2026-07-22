package com.adventofcode.y2018;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day19Test {

    static Day19 day19;

    @BeforeAll
    public static void init() throws IOException {
        day19 = new Day19();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(978L, day19.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(10996992L, day19.part2());
    }
}
