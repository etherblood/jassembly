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
import com.etherblood.circuit.compile.jassembly.Jassembly;

/**
 *
 * @author Philipp
 */
public class CodeGenerator {

    public void generateCode(Program program, Jassembly jassembly) {
        function(program.getFunction(), jassembly);
        jassembly.terminate();
    }

    private void function(FunctionDeclaration function, Jassembly jassembly) {
        returnStatement(function.getStatement(), jassembly);
    }

    private void returnStatement(ReturnStatement statement, Jassembly jassembly) {
        expression(statement.getExpression(), jassembly);
    }

    private void expression(Expression expression, Jassembly jassembly) {
        if (expression instanceof TermExpression) {
            simpleExpression((TermExpression) expression, jassembly);
            return;
        }
        if (expression instanceof BinaryExpression) {
            binaryExpression((BinaryExpression) expression, jassembly);
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void simpleExpression(TermExpression expression, Jassembly jassembly) {
        term(expression.getTerm(), jassembly);
    }

    private void binaryExpression(BinaryExpression expression, Jassembly jassembly) {
        term(expression.getA(), jassembly);
        if (expression.getOperator() != null) {
            jassembly.pushStack();
            term(expression.getB(), jassembly);
            jassembly.toX0();
            jassembly.popStack();
            switch (expression.getOperator()) {
                case ADD:
                    jassembly.add();
                    break;
                case SUBTRACT:
                    jassembly.sub();
                    break;
                default:
                    throw new AssertionError(expression.getOperator().name());

            }
        }
    }

    private void term(Term term, Jassembly jassembly) {
        factor(term.getA(), jassembly);
        if (term.getOperator() != null) {
            //TODO
            throw new UnsupportedOperationException();
        }
    }

    private void factor(Factor factor, Jassembly jassembly) {
        if (factor instanceof LiteralFactor) {
            literalFactor((LiteralFactor) factor, jassembly);
            return;
        }
        if (factor instanceof ExpressionFactor) {
            expressionFactor((ExpressionFactor) factor, jassembly);
            return;
        }
        if (factor instanceof UnaryFactor) {
            unaryFactor((UnaryFactor) factor, jassembly);
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void unaryFactor(UnaryFactor factor, Jassembly jassembly) {
        factor(factor.getFactor(), jassembly);
        switch (factor.getUnaryOperator()) {
            case COMPLEMENT:
                jassembly.complement();
                break;
            case NEGATE:
                jassembly.negate();
                break;
            default:
                throw new AssertionError(factor);
        }
    }

    private void expressionFactor(ExpressionFactor factor, Jassembly jassembly) {
        expression(factor.getExpression(), jassembly);
    }

    private void literalFactor(LiteralFactor factor, Jassembly jassembly) {
        jassembly.constant(factor.getLiteral());
    }
}
