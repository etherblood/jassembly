package com.etherblood.circuit.compile.ast.expression.and;

import com.etherblood.circuit.compile.ast.expression.equality.EqualityExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class SimpleAndExpression implements AndExpression {

    private final EqualityExpression term;

    public SimpleAndExpression(EqualityExpression term) {
        this.term = Objects.requireNonNull(term);
    }

    public EqualityExpression getTerm() {
        return term;
    }

}
