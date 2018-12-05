package com.etherblood.jassembly.compile.jassembly.language.ast.statement;

import com.etherblood.jassembly.compile.jassembly.language.ast.expression.Expression;
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
