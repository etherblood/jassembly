package com.etherblood.jassembly.compile.ast;

import com.etherblood.jassembly.compile.ast.statement.block.Block;

/**
 *
 * @author Philipp
 */
public class FunctionDeclaration {

    private final String identifier;
    private final Block body;
    private final String[] parameters;

    public FunctionDeclaration(String identifier, Block body, String... parameters) {
        this.identifier = identifier;
        this.body = body;
        this.parameters = parameters;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Block getBody() {
        return body;
    }

    public String[] getParameters() {
        return parameters;
    }
}
