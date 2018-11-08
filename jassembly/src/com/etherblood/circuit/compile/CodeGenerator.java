package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.ReturnStatement;
import com.etherblood.circuit.compile.ast.expression.AssignmentExpression;
import com.etherblood.circuit.compile.ast.expression.BinaryOperationExpression;
import com.etherblood.circuit.compile.ast.expression.ConstantExpression;
import com.etherblood.circuit.compile.ast.expression.Expression;
import com.etherblood.circuit.compile.ast.expression.UnaryOperationExpression;
import com.etherblood.circuit.compile.ast.expression.VariableExpression;
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
        if (expression instanceof AssignmentExpression) {
            throw new UnsupportedOperationException();
        }
        if (expression instanceof VariableExpression) {
            throw new UnsupportedOperationException();
        }
        if (expression instanceof ConstantExpression) {
            jassembly.constant(((ConstantExpression) expression).getValue());
            return;
        }
        if (expression instanceof UnaryOperationExpression) {
            UnaryOperationExpression unary = (UnaryOperationExpression) expression;
            expression(unary.getExpression(), jassembly);
            switch (unary.getOperator()) {
                case COMPLEMENT:
                    jassembly.complement();
                    break;
                case NEGATE:
                    jassembly.negate();
                    break;
                default:
                    throw new AssertionError(unary.getOperator());
            }
            return;
        }
        if (expression instanceof BinaryOperationExpression) {
            BinaryOperationExpression binary = (BinaryOperationExpression) expression;
            expression(binary.getA(), jassembly);
            jassembly.pushStack();
            expression(binary.getB(), jassembly);
            jassembly.toX0();
            jassembly.popStack();
            switch (binary.getOperator()) {
                case EQUAL:
                    jassembly.xor();
                    jassembly.any();
                    jassembly.complement();
                    break;
                case NOT_EQUAL:
                    jassembly.xor();
                    jassembly.any();
                    break;
                case ADD:
                    jassembly.add();
                    break;
                case SUB:
                    jassembly.sub();
                    break;
                default:
                    throw new AssertionError(binary.getOperator());
            }
            return;
        }
        throw new UnsupportedOperationException(expression.toString());
    }

}
