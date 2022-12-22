
package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day7Test {

    static Day7 day7;

    @BeforeEach
    public void init() throws IOException {
        day7 = new Day7();
    }

    @Test
    void testPart1() {
        assertEquals(0L, day7.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day7.part2());
    }
}