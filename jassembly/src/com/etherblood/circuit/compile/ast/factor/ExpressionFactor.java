package com.etherblood.circuit.compile.ast.factor;

import com.etherblood.circuit.compile.ast.expression.Expression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class ExpressionFactor implements Factor {

    private final Expression expression;

    public ExpressionFactor(Expression expression) {
        this.expression = Objects.requireNonNull(expression);
    }

    public Expression getExpression() {
        return expression;
    }

}
