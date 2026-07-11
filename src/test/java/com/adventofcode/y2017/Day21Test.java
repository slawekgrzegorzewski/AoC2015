package com.adventofcode.y2017;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Day21Test {

    static Day21 day21;

    @BeforeAll
    public static void init() throws IOException {
        day21 = new Day21();
    }

    @Test
    void testPart1() {
        assertEquals(123L, day21.part1());
    }

    @Test
    void testPart2() {
        assertEquals(1984683L, day21.part2());
    }

    @ParameterizedTest
    @MethodSource("testTransformationData")
    public void testTransformation(char[][] input, char[][] expectedRotation, char[][] expectedVerticalFlip, char[][] expectedHorizontalFlip) {
        assertArrayEquals(expectedRotation, Day21.rotate(input));
        assertArrayEquals(expectedVerticalFlip, Day21.flipVertically(input));
        assertArrayEquals(expectedHorizontalFlip, Day21.flipHorizontally(input));
    }

    public static Stream<Arguments> testTransformationData() {
        return Stream.of(
                Arguments.of(
                        new char[][]{
                                {'1', '2'},
                                {'3', '4'}},
                        new char[][]{
                                {'3', '1'},
                                {'4', '2'}},
                        new char[][]{
                                {'3', '4'},
                                {'1', '2'}},
                        new char[][]{
                                {'2', '1'},
                                {'4', '3'}}
                ),
                Arguments.of(
                        new char[][]{
                                {'1', '2', '3'},
                                {'4', '5', '6'},
                                {'7', '8', '9'}},
                        new char[][]{
                                {'7', '4', '1'},
                                {'8', '5', '2'},
                                {'9', '6', '3'}},
                        new char[][]{
                                {'7', '8', '9'},
                                {'4', '5', '6'},
                                {'1', '2', '3'}},
                        new char[][]{
                                {'3', '2', '1'},
                                {'6', '5', '4'},
                                {'9', '8', '7'}}
                )
        );
    }


    @Test
    void testCopy() {
        char[][] source1 = new char[][]{
                {'1', '2', '3'},
                {'4', '5', '6'},
                {'7', '8', '8'}
        };
        char[][] source2 = new char[][]{
                {'F', 'E', 'D', 'C'},
                {'B', 'A', '9', '8'},
                {'7', '6', '5', '4'},
                {'3', '2', '1', '0'}
        };
        char[][] destination = new char[][]{
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'}
        };
        char[][] result = new char[][]{
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'0', '0', '0', '0', '0', '0', '0', '0'},
                {'1', '2', '3', '0', '0', '0', '0', '0'},
                {'4', '5', '6', '0', 'F', 'E', 'D', 'C'},
                {'7', '8', '8', '0', 'B', 'A', '9', '8'},
                {'0', '0', '0', '0', '7', '6', '5', '4'},
                {'0', '0', '0', '0', '3', '2', '1', '0'}
        };
        Day21.copy(destination, source1, 2, 0, 2);
        Day21.copy(destination, source2, 3, 3, 3);
        assertArrayEquals(result, destination);
    }
}
