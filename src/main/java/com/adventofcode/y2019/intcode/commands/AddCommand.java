package com.adventofcode.y2019.intcode.commands;

public class AddCommand extends Command {
    public CommandResult execute(CommandInput commandInput) {
        int[] memory = commandInput.memory();
        int instructionPointer = commandInput.instructionPointer();
        int firstParameter = getValue(memory, instructionPointer, 1);
        int secondParameter = getValue(memory, instructionPointer, 2);
        int resultAddress = memory[instructionPointer + 3];
        memory[resultAddress] = firstParameter + secondParameter;
        return new CommandResult(4, null);
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}