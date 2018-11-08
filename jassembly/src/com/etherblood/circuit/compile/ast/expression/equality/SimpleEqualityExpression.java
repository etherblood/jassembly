package com.etherblood.circuit.compile.ast.expression.equality;

import com.etherblood.circuit.compile.ast.expression.additive.AdditiveExpression;
import com.etherblood.circuit.compile.ast.expression.relational.RelationalExpression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class SimpleEqualityExpression implements EqualityExpression {

    private final RelationalExpression term;

    public SimpleEqualityExpression(RelationalExpression term) {
        this.term = Objects.requireNonNull(term);
    }

    public RelationalExpression getTerm() {
        return term;
    }

}
