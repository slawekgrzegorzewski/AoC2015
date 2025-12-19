package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day19Test {

    static Day19 day19;

    @BeforeEach
    public void init() throws IOException {
        day19 = new Day19();
    }

    @Test
    void testPart1() throws IOException {
        long part1 = day19.part1();
        System.out.println("part1 = " + part1);
        assertEquals(518L, part1);
    }

    @Test
    void testPart2() throws IOException {
        long part2 = day19.part2();
        System.out.println("part2 = " + part2);
        assertEquals(0L, part2);
    }
}