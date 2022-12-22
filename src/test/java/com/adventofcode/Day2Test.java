package com.adventofcode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day2Test {

    static Day2 day2;

    @BeforeAll
    public static void init() throws IOException {
        day2 = new Day2();
    }

    @Test
    void testPart1() {
        assertEquals(1606483L, day2.part1());
    }

    @Test
    void testPart2() {
        assertEquals(3842356L, day2.part2());
    }
}