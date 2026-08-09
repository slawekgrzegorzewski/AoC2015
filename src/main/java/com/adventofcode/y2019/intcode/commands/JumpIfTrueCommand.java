package com.adventofcode.y2019.intcode.commands;

public class JumpIfTrueCommand extends Command {
    public CommandResult execute(CommandInput commandInput) {
        Memory<Long> memory = commandInput.memory();
        long instructionPointer = commandInput.instructionPointer();
        long firstParameter = getValue(memory, instructionPointer, 1, commandInput.relativeBase());
        long secondParameter = getValue(memory, instructionPointer, 2, commandInput.relativeBase());if (commandInput.debug()) {
            System.out.println("At " + commandInput.instructionPointer() + ": JumpIfTrueCommand: " + commandInput.memory().get(commandInput.instructionPointer()));
            System.out.println("\tRelative base: " + commandInput.relativeBase());
            System.out.println("\tFirst parameter: " + memory.get(instructionPointer + 1));
            System.out.println("\tFirst value: " + firstParameter);
            System.out.println("\tSecond parameter: " + memory.get(instructionPointer + 2));
            System.out.println("\tSecond value: " + secondParameter);
            System.out.println("\tMove pointer by: " + (firstParameter > 0 ? (secondParameter - instructionPointer) : 3L));
        }
        return new CommandResult(firstParameter > 0 ? (secondParameter - instructionPointer) : 3L, commandInput.relativeBase());
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}