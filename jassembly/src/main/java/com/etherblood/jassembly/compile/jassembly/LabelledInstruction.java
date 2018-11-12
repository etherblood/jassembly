package com.etherblood.jassembly.compile.jassembly;

import com.etherblood.jassembly.compile.jassembly.JassemblyContext;
import com.etherblood.jassembly.usability.code.Instruction;

public class LabelledInstruction extends Labelled {

    private final Instruction command;

    public LabelledInstruction(Instruction command) {
        this.command = command;
    }

    public Instruction getCommand() {
        return command;
    }

    @Override
    public int toCode(JassemblyContext context) {
        return command.ordinal();
    }

    @Override
    public String toString() {
        return command.toString() + " " + getLabels();
    }
}
