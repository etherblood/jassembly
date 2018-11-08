package com.etherblood.circuit.compile.ast.expression;

public class AssignmentExpression implements Expression {

    private final String variable;
    private final Expression value;

    public AssignmentExpression(String variable, Expression value) {
        this.variable = variable;
        this.value = value;
    }

    public String getVariable() {
        return variable;
    }

    public Expression getValue() {
        return value;
    }
}
