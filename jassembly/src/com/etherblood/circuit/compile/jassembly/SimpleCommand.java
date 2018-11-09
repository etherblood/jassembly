package com.etherblood.circuit.compile.jassembly;

import com.etherblood.circuit.usability.codes.Command;
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
