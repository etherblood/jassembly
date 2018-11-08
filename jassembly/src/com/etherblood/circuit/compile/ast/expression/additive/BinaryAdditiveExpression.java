package com.etherblood.circuit.compile.ast.expression.additive;

import com.etherblood.circuit.compile.ast.expression.term.TermExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class BinaryAdditiveExpression implements AdditiveExpression {

    private final TermExpression a, b;
    private final AdditiveOperator operator;

    public BinaryAdditiveExpression(TermExpression a, AdditiveOperator operator, TermExpression b) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
        this.operator = Objects.requireNonNull(operator);
    }

    public TermExpression getA() {
        return a;
    }

    public TermExpression getB() {
        return b;
    }

    public AdditiveOperator getOperator() {
        return operator;
    }
}
