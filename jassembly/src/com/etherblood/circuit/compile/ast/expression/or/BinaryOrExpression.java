package com.etherblood.circuit.compile.ast.expression.or;

import com.etherblood.circuit.compile.ast.expression.additive.AdditiveExpression;
import com.etherblood.circuit.compile.ast.expression.and.AndExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class BinaryOrExpression implements OrExpression {

    private final AndExpression a, b;
    private final OrOperator operator;

    public BinaryOrExpression(AndExpression a, OrOperator operator, AndExpression b) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
        this.operator = Objects.requireNonNull(operator);
    }

    public AndExpression getA() {
        return a;
    }

    public AndExpression getB() {
        return b;
    }

    public OrOperator getOperator() {
        return operator;
    }
}
