package com.etherblood.circuit.compile.ast;

import com.etherblood.circuit.compile.ast.factor.Factor;

/**
 *
 * @author Philipp
 */
public class Term {

    private final Factor a, b;
    private final FactorOperator operator;

    public Term(Factor a) {
        this(a, null, null);
    }

    public Term(Factor a, FactorOperator operator, Factor b) {
        assert (operator == null) == (b == null);
        this.a = a;
        this.b = b;
        this.operator = operator;
    }

    public Factor getA() {
        return a;
    }

    public Factor getB() {
        return b;
    }

    public FactorOperator getOperator() {
        return operator;
    }
}
