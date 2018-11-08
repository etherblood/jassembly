package com.etherblood.circuit.compile.ast.expression.factor;

import java.util.Objects;
import com.etherblood.circuit.compile.ast.expression.or.OrExpression;

/**
 *
 * @author Philipp
 */
public class SimpleFactorExpression implements FactorExpression {

    private final OrExpression expression;

    public SimpleFactorExpression(OrExpression expression) {
        this.expression = Objects.requireNonNull(expression);
    }

    public OrExpression getExpression() {
        return expression;
    }

}
