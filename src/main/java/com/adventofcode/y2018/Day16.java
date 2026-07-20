package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

public class Day16 {
    private final ManualData manualData;


    public Day16() throws IOException {
        this.manualData = Input.day16();
    }

    long part1() {
        return (long) extracted()[0];
    }

    private Object[] extracted() {
        Map<String, BiFunction<ProgramState, Command, Integer>> commands = new HashMap<>();
        commands.put("addr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] + ps.registers[command.secondArgument()]);
        commands.put("addi", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] + command.secondArgument());
        commands.put("mulr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] * ps.registers[command.secondArgument()]);
        commands.put("muli", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] * command.secondArgument());
        commands.put("banr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] & ps.registers[command.secondArgument()]);
        commands.put("bani", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] & command.secondArgument());
        commands.put("borr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] | ps.registers[command.secondArgument()]);
        commands.put("bori", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] | command.secondArgument());
        commands.put("setr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()]);
        commands.put("seti", (ps, command) -> ps.registers[command.thirdArgument()] = command.firstArgument());
        commands.put("gtir", (ps, command) -> ps.registers[command.thirdArgument()] = command.firstArgument() > ps.registers[command.secondArgument()] ? 1 : 0);
        commands.put("gtri", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] > command.secondArgument() ? 1 : 0);
        commands.put("gtrr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] > ps.registers[command.secondArgument()] ? 1 : 0);
        commands.put("eqir", (ps, command) -> ps.registers[command.thirdArgument()] = command.firstArgument() == ps.registers[command.secondArgument()] ? 1 : 0);
        commands.put("eqri", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] == command.secondArgument() ? 1 : 0);
        commands.put("eqrr", (ps, command) -> ps.registers[command.thirdArgument()] = ps.registers[command.firstArgument()] == ps.registers[command.secondArgument()] ? 1 : 0);
        Map<Integer, List<String>> numberToPossibleCommand = new HashMap<>();
        int threeOrMorePossibleCommands = 0;
        for (CommandSample sample : manualData.samples()) {
            List<String> possibleCommands = new ArrayList<>();
            for (var command : commands.entrySet()) {
                int[] registers = new int[4];
                System.arraycopy(sample.registersBefore, 0, registers, 0, 4);
                ProgramState ps = new ProgramState(registers, 0);
                command.getValue().apply(ps, sample.command());
                if (Arrays.equals(sample.registersAfter(), ps.registers())) {
                    possibleCommands.add(command.getKey());
                }
            }
            List<String> actualCommand = numberToPossibleCommand.computeIfAbsent(sample.command().instructionCode(), _ -> new ArrayList<>());
            if (actualCommand.isEmpty())
                actualCommand.addAll(possibleCommands);
            else
                actualCommand.retainAll(possibleCommands);

            if (possibleCommands.size() >= 3) threeOrMorePossibleCommands++;
        }
        while (numberToPossibleCommand.values().stream().mapToInt(List::size).anyMatch(s -> s > 1))
            proces(numberToPossibleCommand);


        int[] registers = new int[]{0, 0, 0, 0};
        ProgramState ps = new ProgramState(registers, 0);
        manualData.program()
                .forEach(command -> commands
                        .get(numberToPossibleCommand.get(command.instructionCode()).getFirst())
                        .apply(ps, command));
        return new Object[]{(long)threeOrMorePossibleCommands, (long)ps.registers[0]};
    }

    private static void proces(Map<Integer, List<String>> numberToPossibleCommand) {
        for (List<String> possibleCommand : numberToPossibleCommand.values()) {
            if (possibleCommand.size() == 1) {
                for (List<String> possibleCommand2 : numberToPossibleCommand.values()) {
                    if (possibleCommand2.size() > 1)
                        possibleCommand2.removeAll(possibleCommand);
                }
            }
        }
    }

    long part2() {
        return (long) extracted()[1];
    }

    public record ManualData(List<CommandSample> samples, List<Command> program) {
    }

    public record Command(int instructionCode, int firstArgument, int secondArgument, int thirdArgument) {
    }

    public record CommandSample(int[] registersBefore, int[] registersAfter, Command command) {
    }

    public static final class ProgramState {
        private final int[] registers;
        private int currentPointer;

        public ProgramState(int[] registers, int currentPointer) {
            this.registers = registers;
            this.currentPointer = currentPointer;
        }

        public int[] registers() {
            return registers;
        }

        public int currentPointer() {
            return currentPointer;
        }

        public void jump(int offset) {
            currentPointer += offset;
        }
    }
}
