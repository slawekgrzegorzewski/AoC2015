package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Day12 {
    private final List<Function<ProgramState, ProgramState>> compiledCode;

    public Day12() throws IOException {
        this.compiledCode = compile(Input.day12());
    }

    private List<Function<ProgramState, ProgramState>> compile(List<String> commands) {
        return commands.stream()
                .map(Day12::compileCommand)
                .toList();
    }

    private static Function<ProgramState, ProgramState> compileCommand(String command) {
        Predicate<String> integerPattern = Pattern.compile("-?\\d+").asMatchPredicate();
        String[] commandParts = command.split(" ");
        switch (commandParts[0]) {
            case "cpy" -> {
                Function<ProgramState, Integer> valueToSetGetter = integerPattern.test(commandParts[1])
                        ? _ -> Integer.parseInt(commandParts[1])
                        : programState -> programState.getRegister(commandParts[1]);
                return programState -> programState.setRegister(commandParts[2], valueToSetGetter.apply(programState)).forwardPointer(1);
            }
            case "inc" -> {
                return programState -> programState.setRegister(commandParts[1], programState.getRegister(commandParts[1]) + 1).forwardPointer(1);
            }
            case "dec" -> {
                return programState -> programState.setRegister(commandParts[1], programState.getRegister(commandParts[1]) - 1).forwardPointer(1);
            }
            case "jnz" -> {
                Function<ProgramState, Integer> valueToTestGetter = integerPattern.test(commandParts[1])
                        ? _ -> Integer.parseInt(commandParts[1])
                        : programState -> programState.getRegister(commandParts[1]);
                int jump = Integer.parseInt(commandParts[2]);
                return programState -> valueToTestGetter.apply(programState) == 0 ? programState.forwardPointer(1) : programState.forwardPointer(jump);
            }
        }
        throw new IllegalArgumentException("Invalid instruction");
    }

    long part1() throws IOException {
        return executeProgram(new ProgramState(0, 0, 0, 0, 0));
    }

    long part2() {
        return executeProgram(new ProgramState(0, 0, 0, 1, 0));
    }

    private int executeProgram(ProgramState state) {
        while (state.instructionIndex >= 0 && state.instructionIndex < compiledCode.size()) {
            state = compiledCode.get(state.instructionIndex).apply(state);
        }
        return state.a;
    }

    public record ProgramState(int instructionIndex, int a, int b, int c, int d) {

        public int getRegister(String register) {
            return switch (register) {
                case "a" -> a;
                case "b" -> b;
                case "c" -> c;
                case "d" -> d;
                default -> throw new IllegalArgumentException("Invalid register: " + register);
            };
        }

        public ProgramState forwardPointer(int offset) {
            return new ProgramState(this.instructionIndex + offset, a, b, c, d);
        }

        public ProgramState setRegister(String register, int value) {
            int a = this.a;
            int b = this.b;
            int c = this.c;
            int d = this.d;
            switch (register) {
                case "a" -> a = value;
                case "b" -> b = value;
                case "c" -> c = value;
                case "d" -> d = value;
                default -> throw new IllegalArgumentException("Invalid register: " + register);
            }
            return new ProgramState(this.instructionIndex, a, b, c, d);
        }
    }
}
