package com.adventofcode.y2019.intcode.commands;

public class EndCommand extends Command {
    public CommandResult execute(CommandInput commandInput) {
        if (commandInput.debug()) {
            System.out.println("At " + commandInput.instructionPointer() + ": EndCommand: " + commandInput.memory().get(commandInput.instructionPointer()));
            System.out.println("\tRelative base: " + commandInput.relativeBase());
        }
        return new CommandResult(1, commandInput.relativeBase());
    }

    @Override
    public boolean isEnd() {
        return true;
    }
}