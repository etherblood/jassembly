package com.etherblood.circuit.compile.ast.statement;

import com.etherblood.circuit.compile.ast.expression.Expression;
import java.util.Objects;

public class IfElseStatement implements Statement {

    private final Expression condition;
    private final Statement ifStatement, elseStatement;

    public IfElseStatement(Expression condition, Statement ifStatement, Statement elseStatement) {
        this.condition = Objects.requireNonNull(condition);
        this.ifStatement = Objects.requireNonNull(ifStatement);
        this.elseStatement = elseStatement;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getIfStatement() {
        return ifStatement;
    }

    public Statement getElseStatement() {
        return elseStatement;
    }
}
