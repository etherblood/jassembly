package com.etherblood.circuit.compile.ast.statement;

import com.etherblood.circuit.compile.ast.expression.Expression;
import java.util.Objects;

public class DeclareStatement implements Statement {

    private final String variable;
    private final Expression expression;

    public DeclareStatement(String variable, Expression expression) {
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
