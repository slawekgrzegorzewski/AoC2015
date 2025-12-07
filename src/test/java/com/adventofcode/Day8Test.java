package com.adventofcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day8Test {

    static Day8 day8;

    @BeforeEach
    public void init() throws IOException {
        day8 = new Day8();
    }

    @Test
    void testPart1() {
        assertEquals(1342L, day8.part1());
    }

    @Test
    void testPart2() {
        assertEquals(2074L, day8.part2());
    }
}