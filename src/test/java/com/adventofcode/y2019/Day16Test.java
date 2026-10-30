package com.adventofcode.y2019;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class Day16Test {

    static Day16 day16;

    @BeforeAll
    public static void init() throws IOException {
        day16 = new Day16();
    }

    @Test
    void testPart1() {
        assertArrayEquals(new int[]{3, 4, 8, 4, 1, 6, 9, 0}, day16.part1());
    }

    @Test
    void testPart2() {
        int[] actual = day16.part2();
        System.out.println("actual = " + Arrays.toString(actual));
        assertArrayEquals(new int[]{4, 8, 7, 7, 6, 7, 8, 5}, actual);
    }
}
