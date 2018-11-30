package com.etherblood.jassembly.compile.ast;

import com.etherblood.jassembly.compile.ast.expression.ExpressionType;

/**
 *
 * @author Philipp
 */
public class VariableDetails {

    private final String name;
    private final ExpressionType type;

    public VariableDetails(String name, ExpressionType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ExpressionType getType() {
        return type;
    }
}
