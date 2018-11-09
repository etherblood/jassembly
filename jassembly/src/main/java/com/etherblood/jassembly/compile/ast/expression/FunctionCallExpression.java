package com.etherblood.jassembly.compile.ast.expression;

public class FunctionCallExpression implements Expression {

    private final String name;
    private final Expression[] arguments;

    public FunctionCallExpression(String name, Expression... arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public Expression[] getArguments() {
        return arguments;
    }

}
