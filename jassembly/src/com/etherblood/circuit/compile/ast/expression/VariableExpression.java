package com.etherblood.circuit.compile.ast.expression;

public class VariableExpression implements Expression {

    private final String variable;

    public VariableExpression(String variable) {
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }

}
