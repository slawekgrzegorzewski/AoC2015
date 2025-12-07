package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day14Test {

    static Day14 day14;

    @BeforeEach
    public void init() throws IOException {
        day14 = new Day14();
    }

    @Test
    void testPart1() throws IOException {
        long part1 = day14.part1();
        System.out.println("part1 = " + part1);
        assertEquals(2696L, part1);
    }

    @Test
    void testPart2() throws IOException {
        long part2 = day14.part2();
        System.out.println("part2 = " + part2);
        assertEquals(1084L, part2);
    }
}