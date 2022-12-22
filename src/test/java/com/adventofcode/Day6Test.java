
package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day6Test {

    static Day6 day6;

    @BeforeEach
    public void init() throws IOException {
        day6 = new Day6();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day6.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day6.part2());
    }
}