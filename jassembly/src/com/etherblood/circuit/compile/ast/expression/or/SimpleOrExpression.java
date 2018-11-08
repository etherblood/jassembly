package com.etherblood.circuit.compile.ast.expression.or;

import com.etherblood.circuit.compile.ast.expression.and.AndExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class SimpleOrExpression implements OrExpression {

    private final AndExpression term;

    public SimpleOrExpression(AndExpression term) {
        this.term = Objects.requireNonNull(term);
    }

    public AndExpression getTerm() {
        return term;
    }

}
