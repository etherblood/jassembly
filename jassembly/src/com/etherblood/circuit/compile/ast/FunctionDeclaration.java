package com.etherblood.circuit.compile.ast;

import com.etherblood.circuit.compile.ast.statement.block.Block;

/**
 *
 * @author Philipp
 */
public class FunctionDeclaration {

    private final String identifier;
    private final Block block;

    public FunctionDeclaration(String identifier, Block block) {
        this.identifier = identifier;
        this.block = block;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Block getBlock() {
        return block;
    }
}
