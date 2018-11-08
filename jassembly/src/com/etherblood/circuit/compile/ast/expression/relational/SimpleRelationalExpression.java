package com.etherblood.circuit.compile.ast.expression.relational;

import com.etherblood.circuit.compile.ast.expression.additive.AdditiveExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class SimpleRelationalExpression implements RelationalExpression {

    private final AdditiveExpression term;

    public SimpleRelationalExpression(AdditiveExpression term) {
        this.term = Objects.requireNonNull(term);
    }

    public AdditiveExpression getTerm() {
        return term;
    }

}
