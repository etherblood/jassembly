package com.etherblood.jassembly.compile.jassembly.assembly.expressions;

public class ConstantExpression implements CompileTimeExpression {

    private final int value;

    public ConstantExpression(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

}
