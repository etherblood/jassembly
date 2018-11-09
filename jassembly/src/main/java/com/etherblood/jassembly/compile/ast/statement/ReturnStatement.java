package com.etherblood.jassembly.compile.ast.statement;

import com.etherblood.jassembly.compile.ast.expression.Expression;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class ReturnStatement implements Statement {

    private final Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = Objects.requireNonNull(expression);
    }

    public Expression getExpression() {
        return expression;
    }
}
