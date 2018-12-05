package com.etherblood.jassembly.compile.jassembly.language.ast.statement;

import com.etherblood.jassembly.compile.jassembly.language.ast.expression.Expression;
import java.util.Objects;

public class AssignStatement implements Statement {

    private final String variable;
    private final Expression expression;

    public AssignStatement(String variable, Expression expression) {
        this.variable = Objects.requireNonNull(variable);
        this.expression = Objects.requireNonNull(expression);
    }

    public String getVariable() {
        return variable;
    }

    public Expression getExpression() {
        return expression;
    }
}
