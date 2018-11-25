package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.compile.ast.FunctionDeclaration;
import com.etherblood.jassembly.compile.ast.Program;
import com.etherblood.jassembly.compile.ast.statement.block.Block;
import com.etherblood.jassembly.compile.ast.statement.block.BlockItem;
import com.etherblood.jassembly.compile.ast.statement.ReturnStatement;
import com.etherblood.jassembly.compile.ast.expression.BinaryOperationExpression;
import com.etherblood.jassembly.compile.ast.expression.BinaryOperator;
import com.etherblood.jassembly.compile.ast.expression.ConstantExpression;
import com.etherblood.jassembly.compile.ast.expression.Expression;
import com.etherblood.jassembly.compile.ast.expression.FunctionCallExpression;
import com.etherblood.jassembly.compile.ast.expression.UnaryOperationExpression;
import com.etherblood.jassembly.compile.ast.expression.VariableExpression;
import com.etherblood.jassembly.compile.ast.statement.AssignStatement;
import com.etherblood.jassembly.compile.ast.statement.BreakStatement;
import com.etherblood.jassembly.compile.ast.statement.ContinueStatement;
import com.etherblood.jassembly.compile.ast.statement.block.VariableDeclaration;
import com.etherblood.jassembly.compile.ast.statement.ExpressionStatement;
import com.etherblood.jassembly.compile.ast.statement.IfElseStatement;
import com.etherblood.jassembly.compile.ast.statement.Statement;
import com.etherblood.jassembly.compile.ast.statement.WhileStatement;
import com.etherblood.jassembly.compile.jassembly.assembly.Jassembly;
import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.JassemblyExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.RegisterExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.ExitCode;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.JassemblyInstruction;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Philipp
 */
public class CodeGenerator {

    private final Jassembly code = new Jassembly();

    public void consume(Program program) {
        CodeGenerationContext context = new CodeGenerationContext();
        functionCall(new FunctionCallExpression("main"), context);
        code.terminate(ExitCode.SUCCESS);
        for (FunctionDeclaration function : program.getFunctions()) {
            functionDeclaration(function, context);
        }
    }

    private void functionCall(FunctionCallExpression call, CodeGenerationContext context) {
        for (Expression argument : call.getArguments()) {
            expression(argument, context);
            code.push(Register.AX);
        }
        code.call(call.getName());
        code.mov(Register.CX, Register.AX);
        for (Expression argument : call.getArguments()) {
            code.pop(Register.NONE);
        }
    }

    private void functionDeclaration(FunctionDeclaration function, CodeGenerationContext context) {
        CodeGenerationContext child = context.clearVars();
        String[] parameters = function.getParameters();
        for (int i = parameters.length - 1; i >= 0; i--) {
            child.getVars().declareParameter(parameters[i]);
        }
        code.setLabel(function.getIdentifier());
        code.mov(Register.SB, Register.AX);
        code.push(Register.AX);
        code.mov(Register.SP, Register.AX);
        code.mov(Register.AX, Register.SB);
        block(function.getBody(), child);
        code.terminate(ExitCode.END_OF_FUNCTION);
    }

    private void block(Block block, CodeGenerationContext context) {
        CodeGenerationContext child = context.withNewScope();
        for (BlockItem item : block.getItems()) {
            blockItem(item, child);
        }
        int innerVars = child.getVars().varCount() - context.getVars().varCount();
        for (int i = 0; i < innerVars; i++) {
            code.pop(Register.NONE);
        }
    }

    private void blockItem(BlockItem blockItem, CodeGenerationContext context) {
        if (blockItem instanceof Statement) {
            statement((Statement) blockItem, context);
            return;
        }
        if (blockItem instanceof VariableDeclaration) {
            VariableDeclaration declare = (VariableDeclaration) blockItem;
            context.getVars().declareVariable(declare.getVariable());
            if (declare.getExpression() != null) {
                expression(declare.getExpression(), context);
            } else {
                code.mov(0, Register.AX);
            }
            code.push(Register.AX);
            return;
        }
        throw new UnsupportedOperationException(blockItem.toString());
    }

    private void statement(Statement statement, CodeGenerationContext context) {
        if (statement instanceof AssignStatement) {
            AssignStatement assign = (AssignStatement) statement;
            int variableOffset = context.getVars().getOffset(assign.getVariable());
            expression(assign.getExpression(), context);
            code.mov(Register.AX, Register.CX);

            code.mov(variableOffset, Register.AX);
            code.mov(Register.AX, Register.BX);
            code.mov(Register.SB, Register.AX);
            code.add(ax(), bx());
            code.mov(Register.AX, Register.BX);

            code.mov(Register.CX, Register.AX);
            code.write(ax(), bx());
            return;
        }
        if (statement instanceof ExpressionStatement) {
            expression(((ExpressionStatement) statement).getExpression(), context);
            return;
        }
        if (statement instanceof ReturnStatement) {
            expression(((ReturnStatement) statement).getExpression(), context);
            code.mov(Register.AX, Register.CX);
            code.mov(Register.SB, Register.AX);
            code.mov(Register.AX, Register.SP);
            code.pop(Register.AX);
            code.mov(Register.AX, Register.SB);
            code.ret();
            //result is in CX
            return;
        }
        if (statement instanceof IfElseStatement) {
            IfElseStatement ifElse = (IfElseStatement) statement;
            UUID ifId = UUID.randomUUID();
            String ifBlock = "ifBody-" + ifId;
            String end = "endif-" + ifId;
            
            expression(ifElse.getCondition(), context);
            code.conditionalJump(ax(), ifBlock);
            if (ifElse.getElseStatement() != null) {
                statement(ifElse.getElseStatement(), context);
            }
            code.jump(end);
            code.setLabel(ifBlock);
            statement(ifElse.getIfStatement(), context);
            code.setLabel(end);
            return;
        }
        if (statement instanceof WhileStatement) {
            WhileStatement whileStatement = (WhileStatement) statement;
            UUID whileId = UUID.randomUUID();
            String head = "whileHead-" + whileId;
            String body = "whileBody-" + whileId;
            String end = "endwhile-" + whileId;

            code.jump(head);

            code.setLabel(body);
            statement(whileStatement.getBody(), context.withLoopLabels(head, end));
            
            code.setLabel(head);
            expression(whileStatement.getCondition(), context);
            code.conditionalJump(ax(), body);

            code.setLabel(end);
            return;
        }
        if (statement instanceof BreakStatement) {
            code.mov(context.getLoopEnd(), Register.AX);
            code.jump(ax());
            return;
        }
        if (statement instanceof ContinueStatement) {
            code.mov(context.getLoopStart(), Register.AX);
            code.jump(ax());
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
        if (expression instanceof FunctionCallExpression) {
            functionCall((FunctionCallExpression) expression, context);
            return;
        }
        if (expression instanceof VariableExpression) {
            VariableExpression variable = (VariableExpression) expression;
            int variableOffset = context.getVars().getOffset(variable.getName());
            code.mov(variableOffset, Register.AX);
            code.mov(Register.AX, Register.BX);
            code.mov(Register.SB, Register.AX);
            code.add(ax(), bx());
            code.mov(Register.AX, Register.BX);

            code.read(bx(), Register.AX);
            return;
        }
        if (expression instanceof ConstantExpression) {
            code.mov(((ConstantExpression) expression).getValue(), Register.AX);
            return;
        }
        if (expression instanceof UnaryOperationExpression) {
            UnaryOperationExpression unary = (UnaryOperationExpression) expression;
            expression(unary.getExpression(), context);
            switch (unary.getOperator()) {
                case COMPLEMENT:
                    code.complement();
                    break;
                case NEGATE:
                    code.negate();
                    break;
                case TO_BOOL:
                    code.any();
                    break;
                case TO_INT:
                    code.mov(1, Register.BX);
                    code.and(ax(), bx());
                    break;
                default:
                    throw new AssertionError(unary.getOperator());
            }
            return;
        }
        if (expression instanceof BinaryOperationExpression) {
            BinaryOperationExpression binary = (BinaryOperationExpression) expression;
            if (binary.getOperator() == BinaryOperator.LAZY_OR) {
                UUID lazyOrId = UUID.randomUUID();
                String skip = "lazyOrSkip-" + lazyOrId;

                expression(binary.getA(), context);
                code.mov(Register.AX, Register.CX);
                code.conditionalJump(cx(), skip);
                expression(binary.getB(), context);
                code.mov(Register.AX, Register.CX);

                code.setLabel(skip);
                code.mov(Register.CX, Register.AX);
                return;
            }
            if (binary.getOperator() == BinaryOperator.LAZY_AND) {
                UUID lazyAndId = UUID.randomUUID();
                String skip = "lazyAndSkip-" + lazyAndId;

                expression(binary.getA(), context);
                code.mov(Register.AX, Register.CX);
                code.complement();
                code.conditionalJump(ax(), skip);
                expression(binary.getB(), context);
                code.mov(Register.AX, Register.CX);

                code.setLabel(skip);
                code.mov(Register.CX, Register.AX);
                return;
            }

            expression(binary.getA(), context);
            code.push(Register.AX);
            expression(binary.getB(), context);
            code.mov(Register.AX, Register.BX);
            code.pop(Register.AX);
            switch (binary.getOperator()) {
                case EQUAL:
                    code.xor(ax(), bx());
                    code.any();
                    code.complement();
                    break;
                case NOT_EQUAL:
                    code.xor(ax(), bx());
                    code.any();
                    break;
                case ADD:
                    code.add(ax(), bx());
                    break;
                case SUB:
                    code.sub(ax(), bx());
                    break;
                case OR:
                    code.or(ax(), bx());
                    break;
                case AND:
                    code.and(ax(), bx());
                    break;
                case XOR:
                    code.xor(ax(), bx());
                    break;
                case LSHIFT:
                    code.lshift(ax(), bx());
                    break;
                case RSHIFT:
                    code.rshift(ax(), bx());
                    break;
                case LESS_OR_EQUAL:
                    code.dec();
                case LESS_THAN:
                    code.sub(ax(), bx());
                    code.mov(Register.AX, Register.BX);
                    code.signBit(Register.AX);
                    code.and(ax(), bx());
                    code.any();
                    break;
                case GREATER_THAN:
                    code.dec();
                case GREATER_OR_EQUAL:
                    code.sub(ax(), bx());
                    code.mov(Register.AX, Register.BX);
                    code.signBit(Register.AX);
                    code.and(ax(), bx());
                    code.any();
                    code.complement();
                    break;
                case MULT:
//int mult(int a, int b) {
//    int c = 0;
//    while (bool(a)) {
//        c = c + (a & bool(b & 1));
//        b = b >> 1;
//        a = a << 1;
//    }
//    return c;
//}
                    
                    UUID multId = UUID.randomUUID();
                    String multStart = "multStart-" + multId;
                    String multBody = "multBody-" + multId;
                    String multEnd = "multEnd-" + multId;

                    code.mov(Register.AX, Register.CX);
                    code.mov(Register.SB, Register.AX);
                    code.push(Register.AX);

                    code.mov(0, Register.AX);
                    code.push(Register.AX);

                    code.mov(Register.BX, Register.AX);
                    code.mov(Register.AX, Register.SB);

                    code.setLabel(multStart);
                    code.mov(multBody, Register.AX);
                    code.mov(Register.AX, Register.BX);
                    code.mov(multEnd, Register.AX);
                    code.xor(ax(), bx());
                    code.mov(Register.AX, Register.BX);

                    code.mov(Register.SB, Register.AX);
                    code.any();

                    code.and(ax(), bx());
                    code.mov(Register.AX, Register.BX);
                    code.mov(multEnd, Register.AX);
                    code.xor(ax(), bx());
                    code.jump(ax());

                    code.setLabel(multBody);
                    code.mov(1, Register.AX);
                    code.mov(Register.AX, Register.BX);
                    code.mov(Register.SB, Register.AX);
                    code.and(ax(), bx());
                    code.any();
                    code.mov(Register.AX, Register.BX);
                    code.mov(Register.CX, Register.AX);
                    code.and(ax(), bx());
                    code.mov(Register.AX, Register.BX);

                    code.pop(Register.AX);
                    code.add(ax(), bx());
                    code.push(Register.AX);

                    code.mov(1, Register.AX);
                    code.mov(Register.AX, Register.BX);

                    code.mov(Register.CX, Register.AX);
                    code.lshift(ax(), bx());
                    code.mov(Register.AX, Register.CX);

                    code.mov(Register.SB, Register.AX);
                    code.rshift(ax(), bx());
                    code.mov(Register.AX, Register.SB);

                    code.mov(multStart, Register.AX);
                    code.jump(ax());

                    code.setLabel(multEnd);
                    code.pop(Register.AX);
                    code.mov(Register.AX, Register.BX);
                    code.pop(Register.AX);
                    code.mov(Register.AX, Register.SB);
                    code.mov(Register.BX, Register.AX);
                    break;
                default:
                    throw new AssertionError(binary.getOperator());
            }
            return;
        }
        throw new UnsupportedOperationException(expression.toString());
    }

    private static JassemblyExpression ax() {
        return new RegisterExpression(Register.AX);
    }

    private static JassemblyExpression bx() {
        return new RegisterExpression(Register.BX);
    }

    private static JassemblyExpression cx() {
        return new RegisterExpression(Register.CX);
    }

    private static JassemblyExpression sp() {
        return new RegisterExpression(Register.SP);
    }

    private static JassemblyExpression sb() {
        return new RegisterExpression(Register.SB);
    }

    private static JassemblyExpression ix() {
        return new RegisterExpression(Register.IX);
    }

    private static JassemblyExpression pc() {
        return new RegisterExpression(Register.PC);
    }

    private static JassemblyExpression none() {
        return new RegisterExpression(Register.NONE);
    }

    public List<JassemblyInstruction> getInstructions() {
        return code.getInstructions();
    }

}
