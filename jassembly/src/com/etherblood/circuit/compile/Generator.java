package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.expressions.Constant;
import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.ReturnStatement;
import com.etherblood.circuit.compile.ast.expressions.Expression;
import com.etherblood.circuit.compile.ast.expressions.UnaryOperation;
import com.etherblood.circuit.usability.codes.Command;
import com.etherblood.circuit.usability.codes.programs.CommandConsumer;

/**
 *
 * @author Philipp
 */
public class Generator {

    public void generateCode(Program program, CommandConsumer consumer) {
        generateCode(program.getFunction(), consumer);
        consumer.add(Command.TERMINATE.ordinal());
    }

    public void generateCode(FunctionDeclaration function, CommandConsumer consumer) {
        generateCode(function.getStatement(), consumer);
    }

    public void generateCode(ReturnStatement statement, CommandConsumer consumer) {
        generateCode(statement.getExpression(), consumer);
    }

    public void generateCode(Expression expression, CommandConsumer consumer) {
        if(expression instanceof Constant) {
            generateCode((Constant)expression, consumer);
        } else if(expression instanceof UnaryOperation) {
            generateCode((UnaryOperation)expression, consumer);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void generateCode(Constant constant, CommandConsumer consumer) {
        consumer.add(Command.LOAD_CONST.ordinal());
        consumer.add(constant.getValue());
    }

    public void generateCode(UnaryOperation operation, CommandConsumer consumer) {
        generateCode(operation.getExpression(), consumer);
        switch(operation.getOperator()) {
            case COMPLEMENT:
                consumer.add(Command.INVERT.ordinal());
                break;
            case NEGATION:
                consumer.add(Command.INVERT.ordinal());
                consumer.add(Command.INC.ordinal());
                break;
            default:
                throw new AssertionError(operation);
        }
    }
}
