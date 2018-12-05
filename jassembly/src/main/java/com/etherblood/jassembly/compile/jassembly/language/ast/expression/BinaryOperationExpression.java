package com.etherblood.jassembly.compile.jassembly.language.ast.expression;


public class BinaryOperationExpression implements Expression {
    private final Expression a, b;
    private final BinaryOperator operator;

    public BinaryOperationExpression(Expression a, BinaryOperator operator, Expression b) {
        this.a = a;
        this.operator = operator;
        this.b = b;
    }

    public Expression getA() {
        return a;
    }

    public Expression getB() {
        return b;
    }

    public BinaryOperator getOperator() {
        return operator;
    }
}
