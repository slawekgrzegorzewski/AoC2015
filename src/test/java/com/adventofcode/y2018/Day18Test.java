package com.adventofcode.y2018;


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
        assertEquals(545600L, day18.part1());
    }

    @Test
    void testPart2() {
        assertEquals(202272L, day18.part2());
    }
}
