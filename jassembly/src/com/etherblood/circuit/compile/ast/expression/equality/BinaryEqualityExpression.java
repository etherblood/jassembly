package com.etherblood.circuit.compile.ast.expression.equality;

import com.etherblood.circuit.compile.ast.expression.additive.AdditiveExpression;
import com.etherblood.circuit.compile.ast.expression.relational.RelationalExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class BinaryEqualityExpression implements EqualityExpression {

    private final RelationalExpression a, b;
    private final EqualityOperator operator;

    public BinaryEqualityExpression(RelationalExpression a, EqualityOperator operator, RelationalExpression b) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
        this.operator = Objects.requireNonNull(operator);
    }

    public RelationalExpression getA() {
        return a;
    }

    public RelationalExpression getB() {
        return b;
    }

    public EqualityOperator getOperator() {
        return operator;
    }
}
