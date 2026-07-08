package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.function.IntFunction;

public class Day1 {
    private final char[] sequence;


    public Day1() throws IOException {
        this.sequence = Input.day1();
    }

    long part1() {
        return findSum(i -> (i + 1));
    }

    long part2() {
        return findSum(i -> (i + sequence.length / 2));
    }

    private long findSum(IntFunction<Integer> nextDigit) {
        long sum = 0;
        for (int i = 0; i < sequence.length; i++) {
            if (sequence[i] == sequence[nextDigit.apply(i) % sequence.length]) {
                sum += (sequence[i] - '0');
            }
        }
        return sum;
    }
}


