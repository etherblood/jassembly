package com.etherblood.circuit.compile.ast.expression.factor;

import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class UnaryFactorExpression implements FactorExpression {

    private final UnaryOperator unaryOperator;
    private final FactorExpression factor;

    public UnaryFactorExpression(UnaryOperator unaryOperator, FactorExpression factor) {
        this.unaryOperator = Objects.requireNonNull(unaryOperator);
        this.factor = Objects.requireNonNull(factor);
    }

    public UnaryOperator getUnaryOperator() {
        return unaryOperator;
    }

    public FactorExpression getFactor() {
        return factor;
    }

}
