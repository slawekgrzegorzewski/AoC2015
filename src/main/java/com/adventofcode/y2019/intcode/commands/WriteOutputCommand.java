package com.adventofcode.y2019.intcode.commands;

public class WriteOutputCommand extends Command {
    public CommandResult execute(CommandInput commandInput) throws InterruptedException {
        Memory<Long> memory = commandInput.memory();
        long instructionPointer = commandInput.instructionPointer();
        long firstParameter = getValue(memory, instructionPointer, 1, commandInput.relativeBase());
        if (commandInput.debug()) {
            System.out.println("At " + commandInput.instructionPointer() + ": WriteOutputCommand: " + commandInput.memory().get(commandInput.instructionPointer()));
            System.out.println("\tRelative base: " + commandInput.relativeBase());
            System.out.println("\tRelative base: " + commandInput.relativeBase());
            System.out.println("\tFirst parameter: " + memory.get(instructionPointer + 1));
            System.out.println("\tFirst value: " + firstParameter);
            System.out.println("\tOutput value: " + firstParameter);
        }
        commandInput.onOutput().run();
        commandInput.output().put(firstParameter);
        return new CommandResult(2, commandInput.relativeBase());
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}