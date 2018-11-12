package com.etherblood.jassembly.compile.jassembly;

import com.etherblood.jassembly.compile.jassembly.JassemblyContext;

public class LabelledLiteral extends Labelled {

    private final int value;

    public LabelledLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int toCode(JassemblyContext context) {
        return value;
    }

    @Override
    public String toString() {
        return "0x" + Integer.toHexString(value & 0xffff) + " " + getLabels();
    }
}
