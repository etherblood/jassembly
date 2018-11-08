package com.etherblood.circuit.compile.ast.expression.relational;

import com.etherblood.circuit.compile.ast.expression.additive.AdditiveExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class BinaryRelationalExpression implements RelationalExpression {

    private final AdditiveExpression a, b;
    private final RelationalOperator operator;

    public BinaryRelationalExpression(AdditiveExpression a, RelationalOperator operator, AdditiveExpression b) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
        this.operator = Objects.requireNonNull(operator);
    }

    public AdditiveExpression getA() {
        return a;
    }

    public AdditiveExpression getB() {
        return b;
    }

    public RelationalOperator getOperator() {
        return operator;
    }
}
