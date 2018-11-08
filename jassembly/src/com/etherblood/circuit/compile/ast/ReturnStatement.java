package com.etherblood.circuit.compile.ast;

import com.etherblood.circuit.compile.ast.expression.or.OrExpression;

/**
 *
 * @author Philipp
 */
public class ReturnStatement {

    private final OrExpression expression;

    public ReturnStatement(OrExpression expression) {
        this.expression = expression;
    }

    public OrExpression getExpression() {
        return expression;
    }
}
