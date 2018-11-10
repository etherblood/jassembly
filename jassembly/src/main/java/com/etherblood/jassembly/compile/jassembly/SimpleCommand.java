package com.etherblood.jassembly.compile.jassembly;

import com.etherblood.jassembly.usability.code.Command;
import java.util.List;

public class SimpleCommand extends JassemblyCommand {

    private final Command command;

    public SimpleCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public int toCode(List<JassemblyCommand> contextCommands) {
        return command.ordinal();
    }

    @Override
    public String toString() {
        return command.toString() + " " + getLabels();
    }
}
