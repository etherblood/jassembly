package com.etherblood.jassembly.usability.codes.programs;

public class Commands implements CommandBlock {

    private final int[] commands;

    public Commands(int... commands) {
        this.commands = commands;
    }

    @Override
    public void toCommands(CommandConsumer consumer) {
        for (int command : commands) {
            consumer.add(command);
        }
    }

    @Override
    public int size() {
        return commands.length;
    }

}
