package com.adventofcode.y2017;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day23Test {

    static Day23 day23;

    @BeforeAll
    public static void init() throws IOException {
        day23 = new Day23();
    }

    @Test
    void testPart1() {
        assertEquals(6241L, day23.part1());
    }

    @Test
    void testPart2() {
        assertEquals(909L, day23.part2());
    }
}
