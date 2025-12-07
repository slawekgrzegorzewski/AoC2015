package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day12Test {

    static Day12 day12;

    @BeforeEach
    public void init() throws IOException {
        day12 = new Day12();
    }

    @Test
    void testPart1() throws IOException {
        long part1 = day12.part1();
        System.out.println("part1 = " + part1);
        assertEquals(191164L, part1);
    }

    @Test
    void testPart2() throws IOException {
        long part2 = day12.part2();
        System.out.println("part2 = " + part2);
        assertEquals(87842L, part2);
    }
}