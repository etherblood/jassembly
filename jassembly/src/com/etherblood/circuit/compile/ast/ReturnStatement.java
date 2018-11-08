package com.etherblood.circuit.compile.ast;

import com.etherblood.circuit.compile.ast.expression.Expression;

/**
 *
 * @author Philipp
 */
public class ReturnStatement {

    private final Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
