package com.etherblood.circuit.compile.ast;

/**
 *
 * @author Philipp
 */
public class ReturnStatement {

    private final Constant constant;

    public ReturnStatement(Constant constant) {
        this.constant = constant;
    }

    public Constant getConstant() {
        return constant;
    }
}
