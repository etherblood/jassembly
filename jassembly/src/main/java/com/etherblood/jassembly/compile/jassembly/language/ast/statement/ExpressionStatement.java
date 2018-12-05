package com.etherblood.jassembly.compile.jassembly.language.ast.statement;

import com.etherblood.jassembly.compile.jassembly.language.ast.expression.Expression;
import java.util.Objects;


public class ExpressionStatement implements Statement {

    private final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = Objects.requireNonNull(expression);
    }

    public Expression getExpression() {
        return expression;
    }

}
