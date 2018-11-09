package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.statement.block.Block;
import com.etherblood.circuit.compile.ast.statement.block.BlockItem;
import com.etherblood.circuit.compile.ast.statement.ReturnStatement;
import com.etherblood.circuit.compile.ast.expression.BinaryOperationExpression;
import com.etherblood.circuit.compile.ast.expression.ConstantExpression;
import com.etherblood.circuit.compile.ast.expression.Expression;
import com.etherblood.circuit.compile.ast.expression.UnaryOperationExpression;
import com.etherblood.circuit.compile.ast.expression.VariableExpression;
import com.etherblood.circuit.compile.ast.statement.AssignStatement;
import com.etherblood.circuit.compile.ast.statement.BreakStatement;
import com.etherblood.circuit.compile.ast.statement.ContinueStatement;
import com.etherblood.circuit.compile.ast.statement.block.VariableDeclaration;
import com.etherblood.circuit.compile.ast.statement.ExpressionStatement;
import com.etherblood.circuit.compile.ast.statement.IfElseStatement;
import com.etherblood.circuit.compile.ast.statement.Statement;
import com.etherblood.circuit.compile.ast.statement.WhileStatement;
import com.etherblood.circuit.compile.jassembly.Jassembly;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Philipp
 */
public class CodeGenerator {

    public void generateCode(Program program, Jassembly jassembly) {
        function(program.getFunction(), new CodeGenerationContext(jassembly));
        jassembly.terminate();
    }

    private void function(FunctionDeclaration function, CodeGenerationContext context) {
        context.getJassembly().fromSB();
        context.getJassembly().pushStack();
        context.getJassembly().fromSP();
        context.getJassembly().toSB();
        block(function.getBlock(), context);
    }

    private void block(Block block, CodeGenerationContext context) {
        CodeGenerationContext child = context.withNewScope();
        for (BlockItem item : block.getItems()) {
            blockItem(item, child);
        }
        int innerVars = child.getVars().size() - context.getVars().size();
        for (int i = 0; i < innerVars; i++) {
            context.getJassembly().delStack();
        }
    }

    private void blockItem(BlockItem blockItem, CodeGenerationContext context) {
        if (blockItem instanceof Statement) {
            statement((Statement) blockItem, context);
            return;
        }
        if (blockItem instanceof VariableDeclaration) {
            VariableDeclaration declare = (VariableDeclaration) blockItem;
            if (context.getVars().contains(declare.getVariable())) {
                throw new IllegalStateException("variable '" + declare.getVariable() + "' declared twice.");
            }
            context.getVars().add(declare.getVariable());
            if (declare.getExpression() != null) {
                expression(declare.getExpression(), context);
            } else {
                context.getJassembly().constant(0);
            }
            context.getJassembly().pushStack();
            return;
        }
        throw new UnsupportedOperationException(blockItem.toString());
    }

    private void statement(Statement statement, CodeGenerationContext context) {
        if (statement instanceof AssignStatement) {
            AssignStatement assign = (AssignStatement) statement;
            int variableOffset = variableOffset(context.getVars(), assign.getVariable());
            expression(assign.getExpression(), context);
            context.getJassembly().toX1();

            context.getJassembly().constant(variableOffset);
            context.getJassembly().toX0();
            context.getJassembly().fromSB();
            context.getJassembly().sub();
            context.getJassembly().toX0();

            context.getJassembly().fromX1();
            context.getJassembly().writeRam();
            return;
        }
        if (statement instanceof ExpressionStatement) {
            expression(((ExpressionStatement) statement).getExpression(), context);
            return;
        }
        if (statement instanceof ReturnStatement) {
            expression(((ReturnStatement) statement).getExpression(), context);
            context.getJassembly().toX1();
            context.getJassembly().fromSB();
            context.getJassembly().toSP();
            context.getJassembly().popStack();
            context.getJassembly().toSB();
            //TODO: jump to return address
            context.getJassembly().fromX1();
            return;
        }
        if (statement instanceof IfElseStatement) {
            IfElseStatement ifElse = (IfElseStatement) statement;
            expression(ifElse.getCondition(), context);

            String ifBlock = UUID.randomUUID().toString(), elseBlock = UUID.randomUUID().toString(), end = UUID.randomUUID().toString();

            context.getJassembly().toX1();
            context.getJassembly().constant(ifBlock, 0);
            context.getJassembly().toX0();
            context.getJassembly().constant(elseBlock, 0);
            context.getJassembly().xor();
            context.getJassembly().toX0();

            context.getJassembly().fromX1();
            context.getJassembly().and();
            context.getJassembly().toX0();
            context.getJassembly().constant(elseBlock, 0);
            context.getJassembly().xor();
            context.getJassembly().jump();

            context.getJassembly().labelNext(elseBlock);
            if (ifElse.getElseStatement() != null) {
                statement(ifElse.getElseStatement(), context);
            }
            context.getJassembly().constant(end, 0);
            context.getJassembly().jump();

            context.getJassembly().labelNext(ifBlock);
            statement(ifElse.getIfStatement(), context);
            context.getJassembly().labelNext(end);
            return;
        }
        if (statement instanceof WhileStatement) {
            WhileStatement whileStatement = (WhileStatement) statement;
            String start = UUID.randomUUID().toString(), body = UUID.randomUUID().toString(), end = UUID.randomUUID().toString();

            context.getJassembly().labelNext(start);
            expression(whileStatement.getCondition(), context);

            context.getJassembly().toX1();
            context.getJassembly().constant(body, 0);
            context.getJassembly().toX0();
            context.getJassembly().constant(end, 0);
            context.getJassembly().xor();
            context.getJassembly().toX0();

            context.getJassembly().fromX1();
            context.getJassembly().and();
            context.getJassembly().toX0();
            context.getJassembly().constant(end, 0);
            context.getJassembly().xor();
            context.getJassembly().jump();

            context.getJassembly().labelNext(body);
            statement(whileStatement.getBody(), context.withLoopLabels(start, end));
            context.getJassembly().constant(start, 0);
            context.getJassembly().jump();

            context.getJassembly().labelNext(end);
            return;
        }
        if (statement instanceof BreakStatement) {
            context.getJassembly().constant(context.getLoopEnd(), 0);
            context.getJassembly().jump();
            return;
        }
        if (statement instanceof ContinueStatement) {
            context.getJassembly().constant(context.getLoopStart(), 0);
            context.getJassembly().jump();
            return;
        }
        if (statement instanceof Block) {
            Block block = (Block) statement;
            for (BlockItem item : block.getItems()) {
                blockItem(item, context);
            }
            return;
        }
        throw new UnsupportedOperationException(statement.toString());
    }

    private void expression(Expression expression, CodeGenerationContext context) {
        if (expression instanceof VariableExpression) {
            VariableExpression variable = (VariableExpression) expression;
            int variableOffset = variableOffset(context.getVars(), variable.getVariable());
            context.getJassembly().constant(variableOffset);
            context.getJassembly().toX0();
            context.getJassembly().fromSB();
            context.getJassembly().sub();
            context.getJassembly().toX0();

            context.getJassembly().readRam();
            return;
        }
        if (expression instanceof ConstantExpression) {
            context.getJassembly().constant(((ConstantExpression) expression).getValue());
            return;
        }
        if (expression instanceof UnaryOperationExpression) {
            UnaryOperationExpression unary = (UnaryOperationExpression) expression;
            expression(unary.getExpression(), context);
            switch (unary.getOperator()) {
                case COMPLEMENT:
                    context.getJassembly().complement();
                    break;
                case NEGATE:
                    context.getJassembly().negate();
                    break;
                default:
                    throw new AssertionError(unary.getOperator());
            }
            return;
        }
        if (expression instanceof BinaryOperationExpression) {
            BinaryOperationExpression binary = (BinaryOperationExpression) expression;
            expression(binary.getA(), context);
            context.getJassembly().pushStack();
            expression(binary.getB(), context);
            context.getJassembly().toX0();
            context.getJassembly().popStack();
            switch (binary.getOperator()) {
                case EQUAL:
                    context.getJassembly().xor();
                    context.getJassembly().any();
                    context.getJassembly().complement();
                    break;
                case NOT_EQUAL:
                    context.getJassembly().xor();
                    context.getJassembly().any();
                    break;
                case ADD:
                    context.getJassembly().add();
                    break;
                case SUB:
                    context.getJassembly().sub();
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
            throw new IllegalStateException("tried to assign undeclared variable '" + variable + "'");
        }
        return variableIndex + 1;
    }

}
