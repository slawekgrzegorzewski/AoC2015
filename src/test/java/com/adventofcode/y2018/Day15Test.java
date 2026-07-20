package com.adventofcode.y2018;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day15Test {

    static Day15 day15;

    @BeforeAll
    public static void init() throws IOException {
        day15 = new Day15();
    }


    @Test
    void testPart1() {
        assertEquals(346574L, day15.part1());
    }

    @Test
    void testPart2() {
        assertEquals(0L, day15.part2());
    }

    @ParameterizedTest
    @MethodSource("part1Sources")
    void testPart1MoreCases(String input, int expected) {
        assertEquals(
                expected,
                new Day15(Day15.Battle.parse(Arrays.asList(input.split("\n")))).part1());
    }

    public static Stream<Arguments> part1Sources() {
        return Stream.of(
                Arguments.of("""
                                #######
                                #.G...#
                                #...EG#
                                #.#.#G#
                                #..G#E#
                                #.....#
                                #######""",
                        27730
                ),
                Arguments.of("""
                                #######
                                #G..#E#
                                #E#E.E#
                                #G.##.#
                                #...#E#
                                #...E.#
                                #######""",
                        36334
                ),
                Arguments.of("""
                                #######
                                #E..EG#
                                #.#G.E#
                                #E.##E#
                                #G..#.#
                                #..E#.#
                                #######""",
                        39514
                ),
                Arguments.of("""
                                #######
                                #E.G#.#
                                #.#G..#
                                #G.#.G#
                                #G..#.#
                                #...E.#
                                #######""",
                        27755
                ),
                Arguments.of("""
                                #######
                                #.E...#
                                #.#..G#
                                #.###.#
                                #E#G#G#
                                #...#G#
                                #######""",
                        28944
                ),
                Arguments.of("""
                                #########
                                #G......#
                                #.E.#...#
                                #..##..G#
                                #...##..#
                                #...#...#
                                #.G...G.#
                                #.....G.#
                                #########""",
                        18740
                )
        );
    }
}
