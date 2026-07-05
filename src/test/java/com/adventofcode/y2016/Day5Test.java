package com.adventofcode.y2016;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day5Test {

    static Day5 day5;

    @BeforeAll
    public static void init() throws IOException, NoSuchAlgorithmException {
        day5 = new Day5();
    }

    @Test
    void testPart1() throws NoSuchAlgorithmException {
        assertEquals("1a3099aa", day5.part1());
    }

    @Test
    void testPart2() throws NoSuchAlgorithmException {
        assertEquals("694190cd", day5.part2());
    }
}
