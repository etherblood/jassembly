package com.etherblood.jassembly.compile.jassembly.assembly.instructions;

public class Label extends JassemblyInstruction {

    private final String label;

    public Label(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "Label " + label;
    }
}
