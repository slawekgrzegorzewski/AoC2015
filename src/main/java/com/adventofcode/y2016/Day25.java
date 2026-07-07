package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day25 {
    private final List<Day12.Command> compiledCode;
    private final ArrayList<String> out = new ArrayList<>();


    public Day25() throws IOException {
        this.compiledCode = Input.day25().stream()
                .map(command -> Day12.compileCommand(command, out))
                .toList();
    }

    long part1() {
        out.clear();
        int a = 0;
        executeProgram(compiledCode, new Day12.ProgramState(0, a, 0, 0, 0));
        while (!checkOutput(out)) {
            out.clear();
            a++;
            executeProgram(compiledCode, new Day12.ProgramState(0, a, 0, 0, 0));
        }
        return a;
    }

    private boolean checkOutput(List<String> out) {
        for(int i = 0; i <out.size(); i++) {
            if((i % 2 == 0 && out.get(i).equals("1")) || (i % 2 == 1 && out.get(i).equals("0"))) {
                return false;
            }
        }
        return !out.isEmpty();
    }

    long part2() {
        return 0L;
    }

    private ArrayList<String > executeProgram(List<Day12.Command> program, Day12.ProgramState state) {
        while (state.instructionIndex() >= 0 && state.instructionIndex() < program.size()) {
            Day12.Command command = program.get(state.instructionIndex());
            state = command.execute(program, state);
            if (out.size() > 1000 || (!out.isEmpty() && !checkOutput(out))) {
                break;
            }
        }
        return out;
    }
}
