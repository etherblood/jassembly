package com.etherblood.circuit.compile.ast.statement.block;

import com.etherblood.circuit.compile.ast.statement.Statement;

/**
 *
 * @author Philipp
 */
public class Block implements Statement {

    private final BlockItem[] items;

    public Block(BlockItem[] items) {
        this.items = items;
    }

    public BlockItem[] getItems() {
        return items;
    }
}
