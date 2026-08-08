package com.adventofcode.y2019.intcode.commands;

public class WriteOutputCommand extends Command {
    public CommandResult execute(CommandInput commandInput) throws InterruptedException {
        int[] memory = commandInput.memory();
        int instructionPointer = commandInput.instructionPointer();
        int firstParameter = getValue(memory, instructionPointer, 1);
        commandInput.output().put(firstParameter);
        return new CommandResult(2);
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}