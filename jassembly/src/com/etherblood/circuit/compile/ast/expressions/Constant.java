package com.etherblood.circuit.compile.ast.expressions;

/**
 *
 * @author Philipp
 */
public class Constant implements Expression {

    private final int value;

    public Constant(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
