package com.etherblood.jassembly.compile.ast.statement.block;

import com.etherblood.jassembly.compile.ast.statement.Statement;

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
