package com.adventofcode.y2018;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day14Test {

    static Day14 day14;

    @BeforeAll
    public static void init() throws IOException {
        day14 = new Day14();
    }

    @Test
    void testPart1() {
        assertEquals("1191216109", day14.part1());
    }

    @Test
    void testPart2() {
//        assertEquals("9", new Day14("51589").part2());
//        assertEquals("5", new Day14("01245").part2());
//        assertEquals("18", new Day14("92510").part2());
//        assertEquals("2018", new Day14("59414").part2());
        assertEquals("", day14.part2());
    }
}
