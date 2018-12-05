package com.etherblood.jassembly.compile.jassembly.language.ast.statement.block;

import com.etherblood.jassembly.compile.jassembly.language.ast.VariableDetails;
import com.etherblood.jassembly.compile.jassembly.language.ast.expression.Expression;
import java.util.Objects;

public class VariableDeclaration implements BlockItem {

    private final VariableDetails variable;
    private final Expression expression;

    public VariableDeclaration(VariableDetails variable, Expression expression) {
        this.variable = Objects.requireNonNull(variable);
        this.expression = expression;
    }

    public VariableDetails getVariable() {
        return variable;
    }

    public Expression getExpression() {
        return expression;
    }
}
