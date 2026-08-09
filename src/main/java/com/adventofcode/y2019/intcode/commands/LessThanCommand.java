package com.adventofcode.y2019.intcode.commands;

public class LessThanCommand extends Command {
    public CommandResult execute(CommandInput commandInput) {
        Memory<Long> memory = commandInput.memory();
        long instructionPointer = commandInput.instructionPointer();
        long firstParameter = getValue(memory, instructionPointer, 1, commandInput.relativeBase());
        long secondParameter = getValue(memory, instructionPointer, 2, commandInput.relativeBase());
        long resultAddress = getValueForJump(commandInput.memory(), instructionPointer , 3, commandInput.relativeBase());
        if (commandInput.debug()) {
            System.out.println("At " + commandInput.instructionPointer() + ": LessThanCommand: " + commandInput.memory().get(commandInput.instructionPointer()));
            System.out.println("\tRelative base: " + commandInput.relativeBase());
            System.out.println("\tFirst parameter: " + memory.get(instructionPointer + 1));
            System.out.println("\tFirst value: " + firstParameter);
            System.out.println("\tSecond parameter: " + memory.get(instructionPointer + 2));
            System.out.println("\tSecond value: " + secondParameter);
            System.out.println("\tResult address: " + resultAddress);
            System.out.println("\tResult: " + (firstParameter < secondParameter ? 1L : 0L));
        }
        memory.set(resultAddress, firstParameter < secondParameter ? 1L : 0L);
        return new CommandResult(4, commandInput.relativeBase());
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}