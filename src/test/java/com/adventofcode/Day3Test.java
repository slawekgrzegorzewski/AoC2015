package com.adventofcode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day3Test {

    static Day3 day3;

    @BeforeAll
    public static void init() throws IOException {
        day3 = new Day3();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day3.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day3.part2());
    }
}