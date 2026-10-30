package com.adventofcode.y2019;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(0L, day16.part2());
    }

    @ParameterizedTest
    @MethodSource("inputs")
    void testFFT(int[] input, int phases, int[] expected) {
        int[] result = Day16.fft(input, phases, 0, input.length);
        assertArrayEquals(expected, Arrays.stream(result).limit(expected.length).toArray());
    }

    private static Stream<Arguments> inputs() {
        return Stream.of(
                Arguments.of(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 4, new int[]{0, 1, 0, 2, 9, 4, 9, 8}),
                Arguments.of(new int[]{8, 0, 8, 7, 1, 2, 2, 4, 5, 8, 5, 9, 1, 4, 5, 4, 6, 6, 1, 9, 0, 8, 3, 2, 1, 8, 6, 4, 5, 5, 9, 5}, 100, new int[]{2, 4, 1, 7, 6, 1, 7, 6}),
                Arguments.of(new int[]{1, 9, 6, 1, 7, 8, 0, 4, 2, 0, 7, 2, 0, 2, 2, 0, 9, 1, 4, 4, 9, 1, 6, 0, 4, 4, 1, 8, 9, 9, 1, 7}, 100, new int[]{7, 3, 7, 4, 5, 4, 1, 8}),
                Arguments.of(new int[]{6, 9, 3, 1, 7, 1, 6, 3, 4, 9, 2, 9, 4, 8, 6, 0, 6, 3, 3, 5, 9, 9, 5, 9, 2, 4, 3, 1, 9, 8, 7, 3}, 100, new int[]{5, 2, 4, 3, 2, 1, 3, 3})
        );
    }

    @ParameterizedTest
    @MethodSource("inputs2")
    void testGetPatternElementForPosition(int outputIndex, int inputIndex, int expected) {
        assertEquals(expected, Day16.getPatternElementForPosition(outputIndex, inputIndex, new int[]{0, 1, 0, -1}));
    }

    public static Stream<Arguments> inputs2() {
        return Stream.of(
                Arguments.of(0, 0, 1),
                Arguments.of(0, 1, 0),
                Arguments.of(0, 2, -1),
                Arguments.of(0, 3, 0),
                Arguments.of(0, 4, 1),
                Arguments.of(0, 5, 0),
                Arguments.of(0, 6, -1),
                Arguments.of(0, 7, 0),
                Arguments.of(1, 0, 0),
                Arguments.of(1, 1, 1),
                Arguments.of(1, 2, 1),
                Arguments.of(1, 3, 0),
                Arguments.of(1, 4, 0),
                Arguments.of(1, 5, -1),
                Arguments.of(1, 6, -1),
                Arguments.of(1, 7, 0),
                Arguments.of(1, 8, 0),
                Arguments.of(1, 9, 1),
                Arguments.of(1, 10, 1),
                Arguments.of(1, 11, 0),
                Arguments.of(1, 12, 0),
                Arguments.of(1, 13, -1),
                Arguments.of(1, 14, -1));
    }
}
