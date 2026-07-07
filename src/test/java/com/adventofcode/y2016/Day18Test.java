package com.adventofcode.y2016;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day18Test {

    static Day18 day18;

    @BeforeAll
    public static void init() throws IOException {
        day18 = new Day18();
    }

    @Test
    void testPart1() {
        assertEquals(1951L, day18.part1());
    }

    @Test
    void testPart2() {
        assertEquals(20002936L, day18.part2());
    }
}
