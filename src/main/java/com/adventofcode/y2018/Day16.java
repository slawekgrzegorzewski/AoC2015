package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Day16 {
    public static final Map<String, BiFunction<ProgramState, Command, Integer>> COMMANDS = new HashMap<>();
    private final ManualData manualData;

    static {
        COMMANDS.put("addr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] + ps.registers[command.secondArgument()]);
        COMMANDS.put("addi", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] + command.secondArgument());
        COMMANDS.put("mulr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] * ps.registers[command.secondArgument()]);
        COMMANDS.put("muli", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] * command.secondArgument());
        COMMANDS.put("banr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] & ps.registers[command.secondArgument()]);
        COMMANDS.put("bani", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] & command.secondArgument());
        COMMANDS.put("borr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] | ps.registers[command.secondArgument()]);
        COMMANDS.put("bori", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] | command.secondArgument());
        COMMANDS.put("setr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()]);
        COMMANDS.put("seti", (ps, command) -> ps.registers[command.thirdArgument()] = command.firstArgument());
        COMMANDS.put("gtir", (ps, command) -> ps.registers[command.thirdArgument()] = command.firstArgument() > ps.registers[command.secondArgument()] ? 1 : 0);
        COMMANDS.put("gtri", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] > command.secondArgument() ? 1 : 0);
        COMMANDS.put("gtrr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] > ps.registers[command.secondArgument()] ? 1 : 0);
        COMMANDS.put("eqir", (ps, command) -> ps.registers[command.thirdArgument()] = command.firstArgument() == ps.registers[command.secondArgument()] ? 1 : 0);
        COMMANDS.put("eqri", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] == command.secondArgument() ? 1 : 0);
        COMMANDS.put("eqrr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] == ps.registers[command.secondArgument()] ? 1 : 0);
    }

    public Day16() throws IOException {
        this.manualData = Input.day16();
    }

    long part1() {
        return extracted().countOfCommandsWithThreeOrMoreCandidates();
    }

    long part2() {
        return extracted().stateAfterCodeExecution().registers()[0];
    }

    private Result extracted() {
        Map<Integer, List<String>> numberToPossibleCommand = new HashMap<>();
        int threeOrMorePossibleCommands = 0;
        for (CommandSample sample : manualData.samples()) {
            List<String> possibleCommands = new ArrayList<>();
            COMMANDS.forEach((commandName, action) -> {
                ProgramState ps = sample.before();
                action.apply(ps, sample.command());
                if (Arrays.equals(sample.registersAfter(), ps.registers())) {
                    possibleCommands.add(commandName);
                }
            });
            List<String> candidates = numberToPossibleCommand
                    .computeIfAbsent(
                            sample.command().instructionCode(),
                            _ -> new ArrayList<>());
            if (candidates.isEmpty()) {
                candidates.addAll(possibleCommands);
            } else {
                candidates.retainAll(possibleCommands);
            }
            if (possibleCommands.size() >= 3) threeOrMorePossibleCommands++;
        }

        List<String> knownCommands = numberToPossibleCommand.values().stream()
                .filter(l -> l.size() == 1)
                .map(List::getFirst)
                .collect(Collectors.toCollection(ArrayList::new));
        do {
            numberToPossibleCommand.values()
                    .stream()
                    .filter(l -> l.size() > 1)
                    .peek(l -> l.removeAll(knownCommands))
                    .filter(l -> l.size() == 1)
                    .forEach(knownCommands::addAll);
        } while (knownCommands.size() != COMMANDS.size());


        ProgramState ps = new ProgramState(new int[]{0, 0, 0, 0});
        manualData.program()
                .forEach(command -> COMMANDS
                        .get(numberToPossibleCommand.get(command.instructionCode()).getFirst())
                        .apply(ps, command));
        return new Result(threeOrMorePossibleCommands, ps);
    }

    public record Result(int countOfCommandsWithThreeOrMoreCandidates, ProgramState stateAfterCodeExecution) {
    }

    public record ManualData(List<CommandSample> samples, List<Command> program) {
    }

    public record Command(int instructionCode, int firstArgument, int secondArgument, int thirdArgument) {
    }

    public record CommandSample(int[] registersBefore, int[] registersAfter, Command command) {
        public ProgramState before() {
            int[] registers = new int[4];
            System.arraycopy(registersBefore, 0, registers, 0, 4);
            return new ProgramState(registers);
        }
    }

    public static final class ProgramState {
        private final int[] registers;

        public ProgramState(int[] registers) {
            this.registers = registers;
        }

        public int[] registers() {
            return registers;
        }
    }
}
