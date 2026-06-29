package com.adventofcode.shell;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day20Test {

    static Day20 day20;

    @BeforeAll
    public static void init() throws IOException {
        day20 = new Day20();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day20.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day20.part2());
    }
}
