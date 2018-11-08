package com.etherblood.circuit.compile.ast.expression;

import com.etherblood.circuit.compile.ast.*;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class BinaryExpression implements Expression {

    private final Term a, b;
    private final TermOperator operator;

    public BinaryExpression(Term a, TermOperator operator, Term b) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
        this.operator = Objects.requireNonNull(operator);
    }

    public Term getA() {
        return a;
    }

    public Term getB() {
        return b;
    }

    public TermOperator getOperator() {
        return operator;
    }
}
