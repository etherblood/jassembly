package com.etherblood.circuit.compile.ast.expression.and;

import com.etherblood.circuit.compile.ast.expression.equality.EqualityExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class BinaryAndExpression implements AndExpression {

    private final EqualityExpression a, b;
    private final AndOperator operator;

    public BinaryAndExpression(EqualityExpression a, AndOperator operator, EqualityExpression b) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
        this.operator = Objects.requireNonNull(operator);
    }

    public EqualityExpression getA() {
        return a;
    }

    public EqualityExpression getB() {
        return b;
    }

    public AndOperator getOperator() {
        return operator;
    }
}
