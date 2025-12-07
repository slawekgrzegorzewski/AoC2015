package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day11Test {

    static Day11 day11;

    @BeforeEach
    public void init() throws IOException {
        day11 = new Day11();
    }

    @Test
    void testPart1() throws IOException {
        String part1 = day11.part1();
        System.out.println("part1 = " + part1);
        assertEquals("hxbxxyzz", part1);
    }

    @Test
    void testPart2() throws IOException {
        String part2 = day11.part2();
        System.out.println("part2 = " + part2);
        assertEquals("hxcaabcc", part2);
    }
}