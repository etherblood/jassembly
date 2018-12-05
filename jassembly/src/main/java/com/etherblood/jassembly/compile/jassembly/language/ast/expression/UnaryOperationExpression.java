package com.etherblood.jassembly.compile.jassembly.language.ast.expression;

public class UnaryOperationExpression implements Expression {

    private final UnaryOperator operator;
    private final Expression expression;

    public UnaryOperationExpression(UnaryOperator operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public UnaryOperator getOperator() {
        return operator;
    }
}
