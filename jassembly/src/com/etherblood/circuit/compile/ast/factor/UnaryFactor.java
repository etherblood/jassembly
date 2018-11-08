package com.etherblood.circuit.compile.ast.factor;

import com.etherblood.circuit.compile.ast.UnaryOperator;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class UnaryFactor implements Factor {

    private final UnaryOperator unaryOperator;
    private final Factor factor;

    public UnaryFactor(UnaryOperator unaryOperator, Factor factor) {
        this.unaryOperator = Objects.requireNonNull(unaryOperator);
        this.factor = Objects.requireNonNull(factor);
    }

    public UnaryOperator getUnaryOperator() {
        return unaryOperator;
    }

    public Factor getFactor() {
        return factor;
    }

}
