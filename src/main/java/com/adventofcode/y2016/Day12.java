package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Day12 {
    private final List<Command> compiledCode;

    public Day12() throws IOException {
        this.compiledCode = compile(Input.day12());
    }

    private List<Command> compile(List<String> commands) {
        return commands.stream()
                .map(command -> compileCommand(command, new ArrayList<>()))
                .toList();
    }

    public static Command compileCommand(String command, List<String> out) {
        String[] commandParts = command.split(" ");
        return switch (commandParts[0]) {
            case "cpy" -> cpy(commandParts);
            case "inc" -> inc(commandParts);
            case "dec" -> dec(commandParts);
            case "jnz" -> jnz(commandParts);
            case "tgl" -> tgl(commandParts);
            case "out" -> out(commandParts, out);
            default -> throw new IllegalStateException("Unexpected value: " + commandParts[0]);
        };
    }

    private static @NonNull TwoArgumentsCommand jnz(String[] commandParts) {
        return jnz(Argument.parse(commandParts[1]), Argument.parse(commandParts[2]));
    }

    private static @NonNull TwoArgumentsCommand jnz(Argument argument1, Argument argument2) {
        return new TwoArgumentsCommand(
                "jnz",
                (program, programState, arg1, arg2) -> programState.forwardPointer(arg1.getValue(programState) == 0 ? 1 : arg2.getValue(programState)),
                argument1,
                argument2);
    }

    private static @NonNull OneArgumentCommand dec(String[] commandParts) {
        return dec(Argument.parse(commandParts[1]));
    }

    private static @NonNull OneArgumentCommand dec(Argument argument) {
        return new OneArgumentCommand(
                "dec",
                (program, programState, arg1) -> {
                    if (arg1.getVariableName().isPresent())
                        programState = programState.setRegister(arg1.getVariableName().orElseThrow(), arg1.getValue(programState) - 1);
                    return programState.forwardPointer(1);
                },
                argument);
    }

    private static @NonNull TwoArgumentsCommand cpy(String[] commandParts) {
        return cpy(Argument.parse(commandParts[1]), Argument.parse(commandParts[2]));
    }

    private static @NonNull TwoArgumentsCommand cpy(Argument argument1, Argument argument2) {
        return new TwoArgumentsCommand(
                "cpy",
                (program, programState, arg1, arg2) -> {
                    if (arg2.getVariableName().isPresent())
                        programState = programState.setRegister(arg2.getVariableName().orElseThrow(), arg1.getValue(programState));
                    return programState.forwardPointer(1);
                },
                argument1,
                argument2);
    }

    private static @NonNull OneArgumentCommand inc(String[] commandParts) {
        return inc(Argument.parse(commandParts[1]));
    }

    private static @NonNull OneArgumentCommand inc(Argument argument) {
        return new OneArgumentCommand(
                "inc",
                (program, programState, arg1) -> {
                    if (arg1.getVariableName().isPresent())
                        programState = programState.setRegister(arg1.getVariableName().orElseThrow(), arg1.getValue(programState) + 1);
                    return programState.forwardPointer(1);
                },
                argument);
    }

    private static @NonNull OneArgumentCommand tgl(String[] commandParts) {
        return new OneArgumentCommand(
                commandParts[0],
                (program, programState, arg1) -> {
                    int commandToReplaceIndex = programState.instructionIndex() + arg1.getValue(programState);
                    if (commandToReplaceIndex >= 0 && commandToReplaceIndex < program.size())
                        program.set(commandToReplaceIndex, program.get(commandToReplaceIndex).toggle());
                    return programState.forwardPointer(1);
                },
                Argument.parse(commandParts[1]));
    }

    private static @NonNull OneArgumentCommand out(String[] commandParts, List<String> out) {
        return out(Argument.parse(commandParts[1]), out);
    }

    private static @NonNull OneArgumentCommand out(Argument argument, List<String> out) {
        return new OneArgumentCommand(
                "out",
                (program, programState, arg1) -> {
                    out.add(String.valueOf(arg1.getValue(programState)));
                    return programState.forwardPointer(1);
                },
                argument);
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

        public abstract Command toggle();
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

        @Override
        public Command toggle() {
            return switch (command) {
                case "inc" -> dec(argument);
                case "dec" -> inc(argument);
                case "tgl" -> inc(argument);
                default -> throw new IllegalStateException("Unexpected value: " + command);
            };
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

        @Override
        public Command toggle() {
            return switch (command) {
                case "cpy" -> jnz(argument1, argument2);
                case "jnz" -> cpy(argument1, argument2);
                default -> throw new IllegalStateException("Unexpected value: " + command);
            };
        }
    }

    public static abstract class Argument {
        public abstract int getValue(ProgramState state);

        public abstract Optional<String> getVariableName();

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
        public Optional<String> getVariableName() {
            return Optional.empty();
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
        public Optional<String> getVariableName() {
            return Optional.of(register);
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
