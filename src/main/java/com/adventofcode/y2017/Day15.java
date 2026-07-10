package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.function.Predicate;

public class Day15 {
    private final int[] generatorsInitialValues;


    public Day15() throws IOException {
        this.generatorsInitialValues = Input.day15();
    }

    long part1() {
        return work(40_000_000,
                generatorsInitialValues[0],
                generatorsInitialValues[1],
                _ -> true,
                _ -> true);
    }

    long part2() {
        return work(5_000_000,
                generatorsInitialValues[0],
                generatorsInitialValues[1],
                l -> l << 62 == 0,
                l -> l << 61 == 0);
    }

    private static int work(int repetitions, long generatorA, long generatorB, Predicate<Long> generatorAPredicate, Predicate<Long> generatorBPredicate) {
        int matches = 0;
        for (int i = 0; i < repetitions; i++) {
            do {
                generatorA = (generatorA * 16807) % 2147483647;
            } while (!generatorAPredicate.test(generatorA));
            do {
                generatorB = (generatorB * 48271) % 2147483647;
            } while (!generatorBPredicate.test(generatorB));
            if ((generatorA & 0xffff) == (generatorB & 0xffff))
                matches++;
        }
        return matches;
    }
}
