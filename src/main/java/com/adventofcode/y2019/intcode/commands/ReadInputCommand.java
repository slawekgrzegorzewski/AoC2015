package com.adventofcode.y2019.intcode.commands;

public class ReadInputCommand extends Command {
    public CommandResult execute(CommandInput commandInput) throws InterruptedException {
        Memory<Long> memory = commandInput.memory();
        long instructionPointer = commandInput.instructionPointer();
        long firstParameter = getValueForJump(commandInput.memory(), instructionPointer , 1, commandInput.relativeBase());
        Long take = commandInput.input().take();
        if (commandInput.debug()) {
            System.out.println("At " + commandInput.instructionPointer() + ": ReadInputCommand: " + commandInput.memory().get(commandInput.instructionPointer()));
            System.out.println("\tRelative base: " + commandInput.relativeBase());
            System.out.println("\tFirst parameter: " + memory.get(instructionPointer + 1));
            System.out.println("\tFirst value: " + firstParameter);
            System.out.println("\tInput value: " + take);
        }
        memory.set(firstParameter, take);
        return new CommandResult(2, commandInput.relativeBase());
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}