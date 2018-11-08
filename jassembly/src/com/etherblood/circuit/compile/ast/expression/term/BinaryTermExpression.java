package com.etherblood.circuit.compile.ast.expression.term;

import com.etherblood.circuit.compile.ast.expression.factor.FactorExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class BinaryTermExpression implements TermExpression {

    private final FactorExpression a, b;
    private final TermOperator operator;

    public BinaryTermExpression(FactorExpression a, TermOperator operator, FactorExpression b) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
        this.operator = Objects.requireNonNull(operator);
    }

    public FactorExpression getA() {
        return a;
    }

    public FactorExpression getB() {
        return b;
    }

    public TermOperator getOperator() {
        return operator;
    }
}
