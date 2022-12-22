package com.adventofcode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day1Test {

    static Day1 day1;

    @BeforeAll
    public static void init() throws IOException {
        day1 = new Day1();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day1.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day1.part2());
    }
}