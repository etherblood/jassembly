package com.etherblood.circuit.compile.ast.expression.factor;

/**
 *
 * @author Philipp
 */
public class LiteralFactorExpression implements FactorExpression {

    private final int literal;

    public LiteralFactorExpression(int literal) {
        this.literal = literal;
    }

    public Integer getLiteral() {
        return literal;
    }

}
