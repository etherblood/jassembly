package com.etherblood.circuit.compile.jassembly;

import java.util.List;

public class LiteralCommand extends JassemblyCommand {

    private final int value;

    public LiteralCommand(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int toCode(List<JassemblyCommand> contextCommands) {
        return value;
    }

    @Override
    public String toString() {
        return "0x" + Integer.toHexString(value & 0xffff) + " " + getLabels();
    }
}
