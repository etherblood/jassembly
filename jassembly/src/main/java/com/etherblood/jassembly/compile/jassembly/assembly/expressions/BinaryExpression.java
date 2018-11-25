package com.etherblood.jassembly.compile.jassembly.assembly.expressions;

import com.etherblood.jassembly.compile.jassembly.BinaryOperator;

public class BinaryExpression implements CompileTimeExpression {

    private final CompileTimeExpression a, b;
    private final BinaryOperator operator;

    public BinaryExpression(CompileTimeExpression a, CompileTimeExpression b, BinaryOperator operator) {
        this.a = a;
        this.b = b;
        this.operator = operator;
    }

    public CompileTimeExpression getA() {
        return a;
    }

    public CompileTimeExpression getB() {
        return b;
    }

    public BinaryOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator + "(" + a + ", " + b + ")";
    }

}
