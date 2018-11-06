package com.etherblood.circuit.compile.ast.expressions;

import com.etherblood.circuit.compile.tokens.TokenType;

/**
 *
 * @author Philipp
 */
public class UnaryOperation implements Expression {

    private final TokenType operator;
    private final Expression expression;

    public UnaryOperation(TokenType operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public TokenType getOperator() {
        return operator;
    }

    public Expression getExpression() {
        return expression;
    }
}
