package com.adventofcode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day4Test {

    static Day4 day4;

    @BeforeAll
    public static void init() throws IOException {
        day4 = new Day4();
    }


    @Test
    void testPart1() throws NoSuchAlgorithmException {
        assertEquals(117946L, day4.part1());
    }

    @Test
    void testPart2() throws NoSuchAlgorithmException {
        assertEquals(3938038L, day4.part2());
    }
}