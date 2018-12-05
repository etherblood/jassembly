package com.etherblood.jassembly.compile.jassembly.language.ast.expression;

public class ConstantExpression implements Expression {

    private final int value;
    private final ExpressionType type;

    public ConstantExpression(int value, ExpressionType type) {
        this.value = value;
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public ExpressionType getType() {
        return type;
    }
}
