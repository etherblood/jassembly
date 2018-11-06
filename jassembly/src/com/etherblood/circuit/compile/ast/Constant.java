package com.etherblood.circuit.compile.ast;

/**
 *
 * @author Philipp
 */
public class Constant {

    private final int value;

    public Constant(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
