package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day13Test {

    static Day13 day13;

    @BeforeEach
    public void init() throws IOException {
        day13 = new Day13();
    }

    @Test
    void testPart1() throws IOException {
        long part1 = day13.part1();
        System.out.println("part1 = " + part1);
        assertEquals(664L, part1);
    }

    @Test
    void testPart2() throws IOException {
        long part2 = day13.part2();
        System.out.println(part2);
        assertEquals(640L, part2);
    }
}