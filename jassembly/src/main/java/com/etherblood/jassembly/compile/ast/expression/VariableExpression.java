package com.etherblood.jassembly.compile.ast.expression;

public class VariableExpression implements Expression {

    private final String name;

    public VariableExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
