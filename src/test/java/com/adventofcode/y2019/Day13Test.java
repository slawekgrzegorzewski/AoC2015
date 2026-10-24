package com.adventofcode.y2019;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day13Test {

    static Day13 day13;

    @BeforeAll
    public static void init() throws IOException {
        day13 = new Day13();
    }

    @Test
    void testPart1() {
        assertEquals(253L, day13.part1());
    }

    @Test
    void testPart2() throws InterruptedException {
        assertEquals(12263L, day13.part2());
    }
}
