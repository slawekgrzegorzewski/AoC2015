package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.ProgramResult;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Day5 {
    private final List<Integer> program;


    public Day5() throws IOException {
        this.program = Input.day5();
    }

    long part1() {
        ArrayDeque<Integer> input = new ArrayDeque<>();
        input.add(1);
        ProgramResult programResult = executeProgram(input);
        return programResult.output().getLast();
    }

    long part2() {
        ArrayDeque<Integer> input = new ArrayDeque<>();
        input.add(5);
        ProgramResult programResult = executeProgram(input);
        return programResult.output().getLast();
    }

    private ProgramResult executeProgram(Queue<Integer> input) {
        int maxPosition = this.program.stream().mapToInt(i -> i).max().orElseThrow();
        int[] program = new int[Math.max(maxPosition + 1, this.program.size()) + 4];
        for (int i = 0; i < this.program.size(); i++) {
            program[i] = this.program.get(i);
        }
        return new IntcodeComputer().execute(program, input);
    }
}
