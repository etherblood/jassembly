package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.statement.ReturnStatement;
import com.etherblood.circuit.compile.ast.expression.BinaryOperationExpression;
import com.etherblood.circuit.compile.ast.expression.ConstantExpression;
import com.etherblood.circuit.compile.ast.expression.Expression;
import com.etherblood.circuit.compile.ast.expression.UnaryOperationExpression;
import com.etherblood.circuit.compile.ast.expression.VariableExpression;
import com.etherblood.circuit.compile.ast.statement.AssignStatement;
import com.etherblood.circuit.compile.ast.statement.DeclareStatement;
import com.etherblood.circuit.compile.ast.statement.ExpressionStatement;
import com.etherblood.circuit.compile.ast.statement.Statement;
import com.etherblood.circuit.compile.jassembly.Jassembly;
import java.util.ArrayList;
import java.util.List;

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
        jassembly.fromSB();
        jassembly.pushStack();
        jassembly.fromSP();
        jassembly.toSB();
        List<String> vars = new ArrayList<>();
        for (Statement statement : function.getStatements()) {
            statement(statement, vars, jassembly);
        }
    }

    private void statement(Statement statement, List<String> vars, Jassembly jassembly) {
        if (statement instanceof DeclareStatement) {
            DeclareStatement declare = (DeclareStatement) statement;
            if (vars.contains(declare.getVariable())) {
                throw new IllegalStateException("variable " + declare.getVariable() + " declared twice.");
            }
            vars.add(declare.getVariable());
            if (declare.getExpression() != null) {
                expression(declare.getExpression(), vars, jassembly);
            } else {
                jassembly.constant(0);
            }
            jassembly.pushStack();
            return;
        }
        if (statement instanceof AssignStatement) {
            AssignStatement assign = (AssignStatement) statement;
            int variableOffset = variableOffset(vars, assign.getVariable());
            expression(assign.getExpression(), vars, jassembly);
            jassembly.toX1();

            jassembly.constant(variableOffset);
            jassembly.toX0();
            jassembly.fromSB();
            jassembly.sub();
            jassembly.toX0();

            jassembly.fromX1();
            jassembly.writeRam();
            return;
        }
        if (statement instanceof ExpressionStatement) {
            expression(((ExpressionStatement) statement).getExpression(), vars, jassembly);
            return;
        }
        if (statement instanceof ReturnStatement) {
            expression(((ReturnStatement) statement).getExpression(), vars, jassembly);
            jassembly.toX1();
            jassembly.fromSB();
            jassembly.toSP();
            jassembly.popStack();
            jassembly.toSB();
            //TODO: jump to return address
            jassembly.fromX1();
            return;
        }
        throw new UnsupportedOperationException(statement.toString());
    }

    private void expression(Expression expression, List<String> vars, Jassembly jassembly) {
        if (expression instanceof VariableExpression) {
            VariableExpression variable = (VariableExpression) expression;
            int variableOffset = variableOffset(vars, variable.getVariable());
            jassembly.constant(variableOffset);
            jassembly.toX0();
            jassembly.fromSB();
            jassembly.sub();
            jassembly.toX0();

            jassembly.readRam();
            return;
        }
        if (expression instanceof ConstantExpression) {
            jassembly.constant(((ConstantExpression) expression).getValue());
            return;
        }
        if (expression instanceof UnaryOperationExpression) {
            UnaryOperationExpression unary = (UnaryOperationExpression) expression;
            expression(unary.getExpression(), vars, jassembly);
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
            expression(binary.getA(), vars, jassembly);
            jassembly.pushStack();
            expression(binary.getB(), vars, jassembly);
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

    private static int variableOffset(List<String> vars, String variable) throws IllegalStateException {
        int variableIndex = vars.indexOf(variable);
        if (variableIndex == -1) {
            throw new IllegalStateException("tried to assign undeclared variable " + variable);
        }
        return variableIndex + 1;
    }

}
