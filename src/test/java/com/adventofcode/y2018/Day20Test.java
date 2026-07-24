package com.adventofcode.y2018;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day20Test {

    static Day20 day20;

    @BeforeAll
    public static void init() throws IOException {
        day20 = new Day20();
    }

    @Test
    void testPart1() {
        assertEquals(3983L, day20.part1());
    }

    @Test
    void testPart2() {
        assertEquals(8486L, day20.part2());
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(String regex, long expected) {
        assertEquals(expected, new Day20(regex).part1());
    }

    static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of("^WNE$", 3L),
                Arguments.of("^ENWWW(NEEE|SSE(EE|N))$", 10L),
                Arguments.of("^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$", 18L),
                Arguments.of("^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$", 23L),
                Arguments.of("^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$", 31L));
    }
}
