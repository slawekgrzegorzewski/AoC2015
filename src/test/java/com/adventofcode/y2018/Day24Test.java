package com.adventofcode.y2018;


import com.adventofcode.y2018.input.Input;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Day24Test {

    static Day24 day24;

    @BeforeAll
    public static void init() throws IOException {
        day24 = new Day24();
    }

    @Test
    void testPart1() {
        assertEquals(18532L, day24.part1());
    }

    @Test
    void testPart2() {
        assertEquals(6523L, day24.part2());
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testCases(String input, long part1Expected, long part2Expected) throws IOException {
        assertEquals(
                part1Expected,
                new Day24(Input.day24ParseLines(input.lines().toList())).part1());
        assertEquals(
                part2Expected,
                new Day24(Input.day24ParseLines(input.lines().toList())).part2());
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(
                        """
                                Immune System:
                                17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
                                989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3
                                
                                Infection:
                                801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1
                                4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4
                                """,
                        5216,
                        51)
        );
    }
}
