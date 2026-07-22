package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Day16 {
    public static final Map<String, BiFunction<ProgramState, CommandParameters, Integer>> COMMANDS = new HashMap<>();
    public static final Map<String, BiFunction<ProgramState, CommandParameters, String>> JAVA = new HashMap<>();
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

        JAVA.put("addr", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " + " + toLetter(command.secondArgument()) + ";");
        JAVA.put("addi", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " + " + command.secondArgument() + ";");
        JAVA.put("mulr", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " * " + toLetter(command.secondArgument()) + ";");
        JAVA.put("muli", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " * " + command.secondArgument() + ";");
        JAVA.put("banr", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " & " + toLetter(command.secondArgument()) + ";");
        JAVA.put("bani", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " & " + command.secondArgument() + ";");
        JAVA.put("borr", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " | " + toLetter(command.secondArgument()) + ";");
        JAVA.put("bori", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " | " + command.secondArgument() + ";");
        JAVA.put("setr", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + ";");
        JAVA.put("seti", (_, command) -> toLetter(command.thirdArgument()) + " = " + command.firstArgument() + ";");
        JAVA.put("gtir", (_, command) -> toLetter(command.thirdArgument()) + " = " + command.firstArgument() + " > " + toLetter(command.secondArgument()) + "? 1 : 0" + ";");
        JAVA.put("gtri", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " > " + command.secondArgument() + "? 1 : 0" + ";");
        JAVA.put("gtrr", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " > " + toLetter(command.secondArgument()) + "? 1 : 0" + ";");
        JAVA.put("eqir", (_, command) -> toLetter(command.thirdArgument()) + " = " + command.firstArgument() + " == " + toLetter(command.secondArgument()) + "? 1 : 0" + ";");
        JAVA.put("eqri", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " == " + command.secondArgument() + "? 1 : 0" + ";");
        JAVA.put("eqrr", (_, command) -> toLetter(command.thirdArgument()) + " = " + toLetter(command.firstArgument()) + " == " + toLetter(command.secondArgument()) + "? 1 : 0" + ";");
    }

    public static char toLetter(int index) {
        return (char) ('a' + index);
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
        while (ps.getInstructionPointer() < manualData.program().size()) {
            Command command = manualData.program().get(ps.getInstructionPointer());
            COMMANDS
                    .get(numberToPossibleCommand.get(command.instructionCode()).getFirst())
                    .apply(ps, command);
            ps.setInstructionPointer(ps.getInstructionPointer() + 1);
        }
        return new Result(threeOrMorePossibleCommands, ps);
    }

    public record Result(int countOfCommandsWithThreeOrMoreCandidates, ProgramState stateAfterCodeExecution) {
    }

    public record ManualData(List<CommandSample> samples, List<Command> program) {
    }

    public interface CommandParameters {

        int firstArgument();

        int secondArgument();

        int thirdArgument();
    }

    public static final class Command implements CommandParameters {
        private final int instructionCode;
        private final int firstArgument;
        private final int secondArgument;
        private final int thirdArgument;

        public Command(int instructionCode, int firstArgument, int secondArgument, int thirdArgument) {
            this.instructionCode = instructionCode;
            this.firstArgument = firstArgument;
            this.secondArgument = secondArgument;
            this.thirdArgument = thirdArgument;
        }

        public static Command parse(String value) {
            int[] commandParts = Arrays.stream(value.split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            return new Command(commandParts[0], commandParts[1], commandParts[2], commandParts[3]);
        }

        public int instructionCode() {
            return instructionCode;
        }

        public int firstArgument() {
            return firstArgument;
        }

        public int secondArgument() {
            return secondArgument;
        }

        public int thirdArgument() {
            return thirdArgument;
        }
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
        private int instructionPointer;
        private int instructionPointerBind;

        public ProgramState(int[] registers) {
            this.registers = registers;
            this.instructionPointer = 0;
            this.instructionPointerBind = -1;
        }

        public int[] registers() {
            return registers;
        }

        public int getInstructionPointer() {
            return instructionPointer;
        }

        public void setInstructionPointer(int instructionPointer) {
            this.instructionPointer = instructionPointer;
        }

        public int getInstructionPointerBind() {
            return instructionPointerBind;
        }

        public void setInstructionPointerBind(int instructionPointerBind) {
            this.instructionPointerBind = instructionPointerBind;
        }
    }
}
