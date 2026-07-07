package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Day23 {
    private final List<Day12.Command> compiledCode;


    public Day23() throws IOException {
        this.compiledCode = Input.day23().stream()
                .map(Day12::compileCommand)
                .toList();
    }

    long part1() {
        return executeProgram(new ArrayList<>(compiledCode), new Day12.ProgramState(0, 7, 0, 0, 0));
    }

    long part2() {
        return executeProgram(new ArrayList<>(compiledCode), new Day12.ProgramState(0, 12, 0, 0, 0));
    }

    private int executeProgram(List<Day12.Command> program, Day12.ProgramState state) {
        int steps = 0;
        while (state.instructionIndex() >= 0 && state.instructionIndex() < program.size()) {
            state = program.get(state.instructionIndex()).execute(program, state);
            steps++;
        }
        System.out.println("steps = " + steps);
        return state.a();
    }
}
