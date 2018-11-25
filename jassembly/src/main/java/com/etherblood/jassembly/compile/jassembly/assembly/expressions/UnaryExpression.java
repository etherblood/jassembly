package com.etherblood.jassembly.compile.jassembly.assembly.expressions;

import com.etherblood.jassembly.compile.jassembly.UnaryOperator;

public class UnaryExpression implements CompileTimeExpression {

    private final CompileTimeExpression a;
    private final UnaryOperator operator;

    public UnaryExpression(CompileTimeExpression a, UnaryOperator operator) {
        this.a = a;
        this.operator = operator;
    }

    public CompileTimeExpression getA() {
        return a;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator + "(" + a + ")";
    }

}
