package com.adventofcode.y2018;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Day3Test {

    static Day3 day3;

    @BeforeAll
    public static void init() throws IOException {
        day3 = new Day3();
    }

    @Test
    void testPart1() {
        assertEquals(107043L, day3.part1());
    }

    @Test
    void testPart2() {
        assertEquals(346L, day3.part2());
    }

    @ParameterizedTest
    @MethodSource("intersectProvider")
    void testIntersect(Day3.Claim claim1, Day3.Claim claim2, boolean expectedResult) {
        if (expectedResult)
            assertTrue(claim1.findIntersection(claim2).isPresent());
        else
            assertTrue(claim1.findIntersection(claim2).isEmpty());
    }

    public static Stream<Arguments> intersectProvider() {
        List<Arguments> arguments = new ArrayList<>();
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 7; j++)
                arguments.add(
                        Arguments.of(
                                new Day3.Claim(i, new Day3.Coordinate(3, 3), 3, 3),
                                new Day3.Claim(i, new Day3.Coordinate(i, j), 2, 2),
                                i >= 2 && i <= 5 && j >= 2 && j <= 5));
        arguments.add(
                Arguments.of(
                        new Day3.Claim(0, new Day3.Coordinate(3, 2), 2, 4),
                        new Day3.Claim(0, new Day3.Coordinate(2, 3), 4, 2),
                        true)
        );
        return arguments.stream();
    }

}
