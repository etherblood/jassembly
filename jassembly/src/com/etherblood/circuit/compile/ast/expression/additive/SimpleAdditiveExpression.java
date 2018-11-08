package com.etherblood.circuit.compile.ast.expression.additive;

import com.etherblood.circuit.compile.ast.expression.term.TermExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class SimpleAdditiveExpression implements AdditiveExpression {

    private final TermExpression term;

    public SimpleAdditiveExpression(TermExpression term) {
        this.term = Objects.requireNonNull(term);
    }

    public TermExpression getTerm() {
        return term;
    }

}
