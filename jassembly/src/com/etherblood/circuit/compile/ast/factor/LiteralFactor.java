package com.etherblood.circuit.compile.ast.factor;

/**
 *
 * @author Philipp
 */
public class LiteralFactor implements Factor {

    private final int literal;

    public LiteralFactor(int literal) {
        this.literal = literal;
    }

    public Integer getLiteral() {
        return literal;
    }

}
