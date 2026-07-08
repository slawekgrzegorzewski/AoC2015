package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.function.Function;

public class Day5 {
    private final int[] jumps;


    public Day5() throws IOException {
        this.jumps = Input.day5();
    }

    long part1() {
        return followJumps(getJumpsCopy(), correctionCalculator(false));
    }

    long part2() {
        return followJumps(getJumpsCopy(), correctionCalculator(true));
    }

    private int[] getJumpsCopy() {
        int[] jumpsCopy = new int[jumps.length];
        System.arraycopy(jumps, 0, jumpsCopy, 0, jumps.length);
        return jumpsCopy;
    }

    private static int followJumps(int[] jumpsCopy, Function<Integer, Integer> correctionCalculator) {
        int index = 0;
        int steps = 0;
        while (index < jumpsCopy.length) {
            int jump = jumpsCopy[index];
            jumpsCopy[index] += correctionCalculator.apply(jump);
            index += jump;
            steps++;
        }
        return steps;
    }

    private Function<Integer, Integer> correctionCalculator(boolean part2) {
        return jump -> part2 && jump >= 3 ? -1 : 1;
    }
}
