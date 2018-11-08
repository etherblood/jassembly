package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.ReturnStatement;
import com.etherblood.circuit.compile.ast.expression.Expression;
import com.etherblood.circuit.compile.ast.factor.Factor;
import com.etherblood.circuit.compile.ast.Term;
import com.etherblood.circuit.compile.ast.expression.BinaryExpression;
import com.etherblood.circuit.compile.ast.expression.TermExpression;
import com.etherblood.circuit.compile.ast.factor.ExpressionFactor;
import com.etherblood.circuit.compile.ast.factor.LiteralFactor;
import com.etherblood.circuit.compile.ast.factor.UnaryFactor;
import com.etherblood.circuit.usability.codes.Command;
import com.etherblood.circuit.usability.codes.programs.CommandConsumer;

/**
 *
 * @author Philipp
 */
public class CodeGenerator {

    public void generateCode(Program program, CommandConsumer consumer) {
        function(program.getFunction(), consumer);
        consumer.add(Command.TERMINATE.ordinal());
    }

    private void function(FunctionDeclaration function, CommandConsumer consumer) {
        returnStatement(function.getStatement(), consumer);
    }

    private void returnStatement(ReturnStatement statement, CommandConsumer consumer) {
        expression(statement.getExpression(), consumer);
    }

    private void expression(Expression expression, CommandConsumer consumer) {
        if (expression instanceof TermExpression) {
            simpleExpression((TermExpression) expression, consumer);
            return;
        }
        if (expression instanceof BinaryExpression) {
            binaryExpression((BinaryExpression) expression, consumer);
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void simpleExpression(TermExpression expression, CommandConsumer consumer) {
        term(expression.getTerm(), consumer);
    }

    private void binaryExpression(BinaryExpression expression, CommandConsumer consumer) {
        term(expression.getA(), consumer);
        if (expression.getOperator() != null) {
            consumer.add(Command.DEC_STACK.ordinal());
            consumer.add(Command.WRITE_STACK.ordinal());
            term(expression.getB(), consumer);
            consumer.add(Command.TO_X0.ordinal());
            consumer.add(Command.READ_STACK.ordinal());
            consumer.add(Command.INC_STACK.ordinal());
            switch (expression.getOperator()) {
                case ADD:
                    consumer.add(Command.ADD.ordinal());
                    break;
                case SUBTRACT:
                    consumer.add(Command.SUB.ordinal());
                    break;
                default:
                    throw new AssertionError(expression.getOperator().name());

            }
        }
    }

    private void term(Term term, CommandConsumer consumer) {
        factor(term.getA(), consumer);
        if (term.getOperator() != null) {
            //TODO
            throw new UnsupportedOperationException();
        }
    }

    private void factor(Factor factor, CommandConsumer consumer) {
        if (factor instanceof LiteralFactor) {
            literalFactor((LiteralFactor) factor, consumer);
            return;
        }
        if (factor instanceof ExpressionFactor) {
            expressionFactor((ExpressionFactor) factor, consumer);
            return;
        }
        if (factor instanceof UnaryFactor) {
            unaryFactor((UnaryFactor) factor, consumer);
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void unaryFactor(UnaryFactor factor, CommandConsumer consumer) {
        factor(factor.getFactor(), consumer);
        switch (factor.getUnaryOperator()) {
            case COMPLEMENT:
                consumer.add(Command.INVERT.ordinal());
                break;
            case NEGATE:
                consumer.add(Command.INVERT.ordinal());
                consumer.add(Command.INC.ordinal());
                break;
            default:
                throw new AssertionError(factor);
        }
    }

    private void expressionFactor(ExpressionFactor factor, CommandConsumer consumer) {
        expression(factor.getExpression(), consumer);
    }

    private void literalFactor(LiteralFactor factor, CommandConsumer consumer) {
        consumer.add(Command.LOAD_CONST.ordinal());
        consumer.add(factor.getLiteral());
    }
}
