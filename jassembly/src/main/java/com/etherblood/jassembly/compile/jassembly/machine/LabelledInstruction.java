package com.etherblood.jassembly.compile.jassembly.machine;

import com.etherblood.jassembly.usability.code.MachineInstruction;

public class LabelledInstruction extends Labelled {

    private final MachineInstruction command;

    public LabelledInstruction(MachineInstruction command) {
        this.command = command;
    }

    public MachineInstruction getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return command.toString() + " " + getLabels();
    }
}
