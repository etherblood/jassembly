package com.etherblood.circuit.compile.ast.expression;

import com.etherblood.circuit.compile.ast.Term;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class TermExpression implements Expression {

    private final Term term;

    public TermExpression(Term term) {
        this.term = Objects.requireNonNull(term);
    }

    public Term getTerm() {
        return term;
    }

}
