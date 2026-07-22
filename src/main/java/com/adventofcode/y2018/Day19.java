package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.List;

import static com.adventofcode.y2018.Day16.COMMANDS;

public class Day19 {

    public Day19() throws IOException {
    }

    long part1() throws IOException {
        ProgramAndState programAndState = Input.day19();
        Day16.ProgramState programState = programAndState.state();
        List<Command> program = programAndState.program();
        while (programState.getInstructionPointer() < program.size()) {
            if (programState.getInstructionPointerBind() != -1) {
                programState.registers()[programState.getInstructionPointerBind()] = programState.getInstructionPointer();
            }
            Command command = program.get(programState.getInstructionPointer());
            COMMANDS
                    .get(command.instruction())
                    .apply(programState, command);
            if (programState.getInstructionPointerBind() != -1) {
                programState.setInstructionPointer(programState.registers()[programState.getInstructionPointerBind()] + 1);
            }
        }
        assert programState.registers()[0] == sumOfDivisors(977);
        return programState.registers()[0];
    }

    long part2() throws IOException {
        return sumOfDivisors(10_551_377);
    }

    public int sumOfDivisors(int number) {
        int result = number;
        for (int i = 1; i <= number / 2; i++)
            if (number % i == 0)
                result += i;
        return result;
    }

    public record Command(String instruction, int firstArgument, int secondArgument,
                          int thirdArgument) implements Day16.CommandParameters {
        public static Command parse(String value) {
            String[] commandParts = value.split(" ");
            return new Command(
                    commandParts[0],
                    Integer.parseInt(commandParts[1]),
                    Integer.parseInt(commandParts[2]),
                    Integer.parseInt(commandParts[3]));
        }
    }

    public record ProgramAndState(List<Command> program, Day16.ProgramState state) {

    }
}