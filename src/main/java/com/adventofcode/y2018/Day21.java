package com.adventofcode.y2018;

import com.adventofcode.Utils;
import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import static com.adventofcode.y2018.Day16.JAVA;

public class Day21 {

    public Day21() throws IOException {
        Day19.ProgramAndState programAndState = Input.day21();
        Day16.ProgramState programState = programAndState.state();
        List<Day19.Command> program = programAndState.program();

        for (int i = 0; i < program.size(); i++) {
            Day19.Command command = program.get(i);
            System.out.println("/* " + i + ": */" + JAVA.get(command.instruction()).apply(programState, command));
        }
    }

    long part1() {
        int firstExpectedNumber = findExpectedValue(0);
        long countedInstructions = countInstructions(firstExpectedNumber);
        if (countedInstructions != 1849) {
            throw new RuntimeException("Expected 1849L but got " + countedInstructions);
        }
        return firstExpectedNumber;
    }

    long part2() {
        Utils.LoopFinder.LoopInfo<Integer> loop = new Utils.LoopFinder<Integer, Integer>(
                new Supplier<>() {
                    int b = 0;

                    @Override
                    public Integer get() {
                        b = findExpectedValue(b);
                        return b;
                    }
                },
                i -> i,
                5)
                .findLoop()
                .orElseThrow();
        int result = loop.analyzedList().get(loop.loopStart() + loop.loopSize() - 1);
        long instructionCount = countInstructions(result);
        if (instructionCount != 2396789320L) {
            throw new RuntimeException("Expected 1849L but got " + instructionCount);
        }
        return result;
    }

    private int findExpectedValue(int initB) {
        int b = initB;
        int a = b | (1 << 16);
        b = 10736359;
        for (; a >= 1; a /= 256) {
            b = ((b + a % 256) * 65899) & 0xffffff;
        }
        return b;
    }


    private long countInstructions(int expected) {
        int b = 0;
        long instructionsExecuted = 6;
        do {
            int a = b | (1 << 16);
            b = 10736359;
            instructionsExecuted += 2;
            do {
                b = b + (a & 0b11111111);
                b = b & 0b111111111111111111111111;
                b = b * 65899;
                b = b & 0b111111111111111111111111;
                instructionsExecuted += 7;
                if (a < 256) {
                    instructionsExecuted++;
                    break;
                }
                a = a >> 8;
                instructionsExecuted += 7 * a + 9;
            } while (true);
            instructionsExecuted += 2;
            if (b == expected) break;
            instructionsExecuted += 1;
        } while (true);
        return instructionsExecuted;
    }
}
