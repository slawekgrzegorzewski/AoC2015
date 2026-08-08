package com.adventofcode.y2019.intcode.commands;

public class ReadInputCommand extends Command {
    public CommandResult execute(CommandInput commandInput) throws InterruptedException {
        int[] memory = commandInput.memory();
        int instructionPointer = commandInput.instructionPointer();
        int firstParameter = memory[instructionPointer + 1];
        memory[firstParameter] = commandInput.input().take();
        return new CommandResult(2);
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}