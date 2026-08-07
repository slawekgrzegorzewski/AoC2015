package com.adventofcode.y2019.intcode.commands;

public class WriteOutputCommand extends Command {
    public CommandResult execute(CommandInput commandInput) {
        int[] memory = commandInput.memory();
        int instructionPointer = commandInput.instructionPointer();
        int firstParameter = getValue(memory, instructionPointer, 1);
        return new CommandResult(2, firstParameter);
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}