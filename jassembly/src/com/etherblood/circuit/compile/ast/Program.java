package com.etherblood.circuit.compile.ast;

/**
 *
 * @author Philipp
 */
public class Program {

    private final FunctionDeclaration function;

    public Program(FunctionDeclaration function) {
        this.function = function;
    }

    public FunctionDeclaration getFunction() {
        return function;
    }
}
