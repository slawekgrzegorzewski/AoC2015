package com.adventofcode.y2018;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day16Test {

    static Day16 day16;

    @BeforeAll
    public static void init() throws IOException {
        day16 = new Day16();
    }

    @Test
    void testPart1() {
        assertEquals(563L, day16.part1());
    }

    @Test
    void testPart2() {
        assertEquals(629L, day16.part2());
    }
}
