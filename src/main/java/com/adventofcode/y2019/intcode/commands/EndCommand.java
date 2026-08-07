package com.adventofcode.y2019.intcode.commands;

public class EndCommand extends Command {
    public CommandResult execute(CommandInput commandInput) {
        return new CommandResult(1, null);
    }

    @Override
    public boolean isEnd() {
        return true;
    }
}