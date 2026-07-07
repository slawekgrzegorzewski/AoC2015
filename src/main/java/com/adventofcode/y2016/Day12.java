package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Day12 {
    private final List<Command> compiledCode;

    public Day12() throws IOException {
        this.compiledCode = compile(Input.day12());
    }

    private List<Command> compile(List<String> commands) {
        return commands.stream()
                .map(Day12::compileCommand)
                .toList();
    }

    public static Command compileCommand(String command) {
        Predicate<String> integerPattern = Pattern.compile("-?\\d+").asMatchPredicate();
        String[] commandParts = command.split(" ");
        switch (commandParts[0]) {
            case "cpy" -> {
                return new TwoArgumentsCommand(
                        "cpy",
                        (program, programState, arg1, arg2) -> programState.setRegister(arg2.getVariableName(), arg1.getValue(programState)).forwardPointer(1),
                        Argument.parse(commandParts[1]),
                        Argument.parse(commandParts[2]));
            }
            case "inc" -> {
                return new OneArgumentCommand(
                        "inc",
                        (program, programState, arg1) -> programState.setRegister(arg1.getVariableName(), arg1.getValue(programState) + 1).forwardPointer(1),
                        Argument.parse(commandParts[1]));

            }
            case "dec" -> {
                return new OneArgumentCommand(
                        "inc",
                        (program, programState, arg1) -> programState.setRegister(arg1.getVariableName(), arg1.getValue(programState) - 1).forwardPointer(1),
                        Argument.parse(commandParts[1]));
            }
            case "jnz" -> {
                return new TwoArgumentsCommand(
                        "jnz",
                        (program, programState, arg1, arg2) -> programState.forwardPointer(arg1.getValue(programState) == 0 ? 1 : arg2.getValue(programState)),
                        Argument.parse(commandParts[1]),
                        Argument.parse(commandParts[2]));
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
            state = compiledCode.get(state.instructionIndex).execute(compiledCode, state);
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

    public abstract static class Command {
        final String command;

        protected Command(String command) {
            this.command = command;
        }

        public abstract ProgramState execute(List<Command> program, ProgramState state);
    }

    public static class OneArgumentCommand extends Command {
        private final OneArgumentCommandExecutor executor;
        final Argument argument;

        public OneArgumentCommand(String command, OneArgumentCommandExecutor executor, Argument argument) {
            super(command);
            this.executor = executor;
            this.argument = argument;
        }

        @Override
        public ProgramState execute(List<Command> program, ProgramState state) {
            return executor.execute(program, state, argument);
        }
    }

    public static class TwoArgumentsCommand extends Command {

        private final TwoArgumentsCommandExecutor executor;
        final Argument argument1;
        final Argument argument2;

        public TwoArgumentsCommand(String command, TwoArgumentsCommandExecutor executor, Argument argument1, Argument argument2) {
            super(command);
            this.executor = executor;
            this.argument1 = argument1;
            this.argument2 = argument2;
        }

        @Override
        public ProgramState execute(List<Command> program, ProgramState state) {
            return executor.execute(program, state, argument1, argument2);
        }
    }

    public static abstract class Argument {
        public abstract int getValue(ProgramState state);

        public abstract String getVariableName();

        public static Argument parse(String argument) {
            if (argument.matches("[a-d]")) {
                return new Variable(argument);
            }
            return new Constant(Integer.parseInt(argument));
        }
    }

    public static class Constant extends Argument {
        private final int value;

        public Constant(int value) {
            this.value = value;
        }

        @Override
        public int getValue(ProgramState state) {
            return value;
        }

        @Override
        public String getVariableName() {
            throw new UnsupportedOperationException();
        }
    }

    public static class Variable extends Argument {
        private final String register;

        public Variable(String register) {
            this.register = register;
        }

        @Override
        public int getValue(ProgramState state) {
            return state.getRegister(register);
        }

        @Override
        public String getVariableName() {
            return register;
        }
    }

    @FunctionalInterface
    public interface OneArgumentCommandExecutor {

        ProgramState execute(List<Command> program, ProgramState programState, Argument arg1);
    }

    @FunctionalInterface
    public interface TwoArgumentsCommandExecutor {

        ProgramState execute(List<Command> program, ProgramState programState, Argument arg1, Argument arg2);
    }

}
