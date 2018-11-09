package com.etherblood.circuit.compile.ast;

import com.etherblood.circuit.compile.ast.statement.Statement;

/**
 *
 * @author Philipp
 */
public class FunctionDeclaration {

    private final String identifier;
    private final Statement[] statements;

    public FunctionDeclaration(String identifier, Statement[] statements) {
        this.identifier = identifier;
        this.statements = statements;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Statement[] getStatements() {
        return statements;
    }
}
