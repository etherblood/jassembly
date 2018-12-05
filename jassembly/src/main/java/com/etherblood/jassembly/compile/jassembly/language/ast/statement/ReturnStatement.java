package com.etherblood.jassembly.compile.jassembly.language.ast.statement;

import com.etherblood.jassembly.compile.jassembly.language.ast.expression.Expression;
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
