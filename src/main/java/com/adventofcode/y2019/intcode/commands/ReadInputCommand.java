package com.adventofcode.y2019.intcode.commands;

public class ReadInputCommand extends Command {
    public CommandResult execute(CommandInput commandInput) {
        int[] memory = commandInput.memory();
        int instructionPointer = commandInput.instructionPointer();
        int firstParameter = memory[instructionPointer + 1];
        memory[firstParameter] = commandInput.input().remove();
        return new CommandResult(2, null);
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}