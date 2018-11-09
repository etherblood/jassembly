package com.etherblood.jassembly.compile.ast;

/**
 *
 * @author Philipp
 */
public class Program {

    private final FunctionDeclaration[] functions;

    public Program(FunctionDeclaration... functions) {
        this.functions = functions;
    }

    public FunctionDeclaration[] getFunctions() {
        return functions;
    }
}
