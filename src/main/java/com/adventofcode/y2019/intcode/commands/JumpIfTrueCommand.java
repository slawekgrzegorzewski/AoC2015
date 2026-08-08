package com.adventofcode.y2019.intcode.commands;

public class JumpIfTrueCommand extends Command {
    public CommandResult execute(CommandInput commandInput) {
        int[] memory = commandInput.memory();
        int instructionPointer = commandInput.instructionPointer();
        int firstParameter = getValue(memory, instructionPointer, 1);
        int secondParameter = getValue(memory, instructionPointer, 2);
        return new CommandResult(firstParameter > 0 ? (secondParameter - instructionPointer) : 3);
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}