package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.compile.ast.expression.ExpressionType;

/**
 *
 * @author Philipp
 */
public class VariableDeclaration {

    private final String name;
    private final ExpressionType type;
    private final int offset;

    public VariableDeclaration(String name, ExpressionType type, int offset) {
        this.name = name;
        this.type = type;
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public ExpressionType getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }
}
