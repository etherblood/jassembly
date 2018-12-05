package com.etherblood.jassembly.compile.jassembly.language.ast;

import com.etherblood.jassembly.compile.jassembly.language.ast.expression.ExpressionType;
import com.etherblood.jassembly.compile.jassembly.language.ast.statement.block.Block;

/**
 *
 * @author Philipp
 */
public class FunctionDeclaration {

    private final String identifier;
    private final ExpressionType returnType;
    private final Block body;
    private final VariableDetails[] parameters;

    public FunctionDeclaration(String identifier, ExpressionType returnType, Block body, VariableDetails... parameters) {
        this.identifier = identifier;
        this.returnType = returnType;
        this.body = body;
        this.parameters = parameters;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ExpressionType getReturnType() {
        return returnType;
    }

    public Block getBody() {
        return body;
    }

    public VariableDetails[] getParameters() {
        return parameters;
    }
}
