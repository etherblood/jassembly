package com.etherblood.circuit.compile.ast.expression.term;

import com.etherblood.circuit.compile.ast.expression.factor.FactorExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class SimpleTermExpression implements TermExpression {

    private final FactorExpression factor;

    public SimpleTermExpression(FactorExpression factor) {
        this.factor = Objects.requireNonNull(factor);
    }

    public FactorExpression getFactor() {
        return factor;
    }

}
