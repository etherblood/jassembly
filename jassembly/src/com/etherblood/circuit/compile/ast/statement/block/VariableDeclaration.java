package com.etherblood.circuit.compile.ast.statement.block;

import com.etherblood.circuit.compile.ast.expression.Expression;
import java.util.Objects;

public class VariableDeclaration implements BlockItem {

    private final String variable;
    private final Expression expression;

    public VariableDeclaration(String variable, Expression expression) {
        this.variable = Objects.requireNonNull(variable);
        this.expression = expression;
    }

    public String getVariable() {
        return variable;
    }

    public Expression getExpression() {
        return expression;
    }
}
