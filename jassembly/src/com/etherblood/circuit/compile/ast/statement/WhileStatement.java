package com.etherblood.circuit.compile.ast.statement;

import com.etherblood.circuit.compile.ast.expression.Expression;
import java.util.Objects;

public class WhileStatement implements Statement {

    private final Expression condition;
    private final Statement body;

    public WhileStatement(Expression condition, Statement body) {
        this.condition = Objects.requireNonNull(condition);
        this.body = Objects.requireNonNull(body);
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }
}
