package com.adventofcode.y2016;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day17Test {

    static Day17 day17;

    @BeforeAll
    public static void init() throws IOException, NoSuchAlgorithmException {
        day17 = new Day17();
    }

    @Test
    void testPart1() {
        assertEquals("RLDUDRDDRR", day17.part1());
    }

    @Test
    void testPart2() {
        assertEquals(590L, day17.part2());
    }
}
