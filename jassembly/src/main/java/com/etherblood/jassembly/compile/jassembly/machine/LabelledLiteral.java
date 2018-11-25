package com.etherblood.jassembly.compile.jassembly.machine;

public class LabelledLiteral extends Labelled {

    private final int value;

    public LabelledLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "0x" + Integer.toHexString(value & 0xffff) + " " + getLabels();
    }
}
