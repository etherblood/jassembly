package com.etherblood.circuit.compile.ast.expression;

public class ConstantExpression implements Expression {

    private final int value;

    public ConstantExpression(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
