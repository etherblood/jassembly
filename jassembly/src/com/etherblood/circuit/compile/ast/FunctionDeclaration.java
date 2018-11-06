package com.etherblood.circuit.compile.ast;

/**
 *
 * @author Philipp
 */
public class FunctionDeclaration {

    private final String identifier;
    private final ReturnStatement statement;

    public FunctionDeclaration(String identifier, ReturnStatement statement) {
        this.identifier = identifier;
        this.statement = statement;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ReturnStatement getStatement() {
        return statement;
    }
}
