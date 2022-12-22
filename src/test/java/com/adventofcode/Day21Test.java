package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day21Test {

    static Day21 day21;

    @BeforeEach
    public void init() throws IOException {
        day21 = new Day21();
    }

    @Test
    void testPart1() throws IOException {
        assertEquals(0L, day21.part1());
    }

    @Test
    void testPart2() throws IOException {
        assertEquals(0L, day21.part2());
    }
}