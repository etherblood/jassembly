package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.compile.ast.FunctionDeclaration;
import com.etherblood.jassembly.compile.ast.Program;
import com.etherblood.jassembly.compile.ast.VariableDetails;
import com.etherblood.jassembly.compile.ast.statement.block.Block;
import com.etherblood.jassembly.compile.ast.statement.block.BlockItem;
import com.etherblood.jassembly.compile.ast.statement.ReturnStatement;
import com.etherblood.jassembly.compile.ast.expression.BinaryOperationExpression;
import com.etherblood.jassembly.compile.ast.expression.BinaryOperator;
import com.etherblood.jassembly.compile.ast.expression.ConstantExpression;
import com.etherblood.jassembly.compile.ast.expression.Expression;
import com.etherblood.jassembly.compile.ast.expression.ExpressionType;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Philipp
 */
public class CodeGenerator {

    private final Program program;
    private final Jassembly code = new Jassembly();

    public CodeGenerator(Program program) {
        this.program = program;
        CodeGenerationContext context = new CodeGenerationContext();
        functionCall(new FunctionCallExpression("main"), context);
        code.terminate(ExitCode.SUCCESS);
        for (FunctionDeclaration function : program.getFunctions()) {
            functionDeclaration(function, context);
        }
    }

    private ExpressionType functionCall(FunctionCallExpression call, CodeGenerationContext context) {
        FunctionDeclaration function = program.getFunction(call.getName());
        VariableDetails[] parameters = function.getParameters();
        Expression[] arguments = call.getArguments();
        if (parameters.length != arguments.length) {
            throw new UnsupportedOperationException("Function " + function.getIdentifier() + " called with " + arguments.length + " arguments when " + parameters.length + " were expected.");
        }
        for (int i = 0; i < arguments.length; i++) {
            ExpressionType paramType = parameters[i].getType();
            ExpressionType argType = expression(arguments[i], context);
            if (argType != paramType) {
                throw new UnsupportedOperationException("Function " + function.getIdentifier() + " called with " + argType + " as " + i + " argument when " + paramType + " was expected.");
            }
            code.push(Register.AX);
        }
        code.call(call.getName());
        code.mov(Register.CX, Register.AX);
        for (Expression argument : arguments) {
            code.pop(Register.NONE);
        }
        return function.getReturnType();
    }

    private void functionDeclaration(FunctionDeclaration function, CodeGenerationContext context) {
        CodeGenerationContext child = context.withFunctionScope(function);
        VariableDetails[] parameters = function.getParameters();
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
                ExpressionType valueType = expression(declare.getExpression(), context);
                if (declare.getVariable().getType() != valueType) {
                    throw new UnsupportedOperationException();
                }
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
            VariableMeta variable = context.getVars().getDetails(assign.getVariable());
            ExpressionType valueType = expression(assign.getExpression(), context);
            if (variable.getType() != valueType) {
                throw new UnsupportedOperationException();
            }
            code.mov(Register.AX, Register.CX);

            code.mov(variable.getOffset(), Register.AX);
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
            ExpressionType type = expression(((ReturnStatement) statement).getExpression(), context);
            ExpressionType returnType = context.getFunction().getReturnType();
            if (type != returnType) {
                throw new UnsupportedOperationException();
            }
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

    private ExpressionType expression(Expression expression, CodeGenerationContext context) {
        if (expression instanceof FunctionCallExpression) {
            return functionCall((FunctionCallExpression) expression, context);
        }
        if (expression instanceof VariableExpression) {
            VariableExpression variable = (VariableExpression) expression;
            VariableMeta details = context.getVars().getDetails(variable.getName());
            code.mov(details.getOffset(), Register.AX);
            code.add(ax(), sb());
            code.read(ax(), Register.AX);
            return details.getType();
        }
        if (expression instanceof ConstantExpression) {
            ConstantExpression constant = (ConstantExpression) expression;
            code.mov(constant.getValue(), Register.AX);
            return constant.getType();
        }
        if (expression instanceof UnaryOperationExpression) {
            UnaryOperationExpression unary = (UnaryOperationExpression) expression;
            ExpressionType type = expression(unary.getExpression(), context);
            switch (unary.getOperator()) {
                case COMPLEMENT:
                    code.complement();
                    return type;
                case NEGATE:
                    requireType(type, "Can't negate " + type + ".", ExpressionType.UINT, ExpressionType.SINT);
                    code.negate();
                    return type;
                case ANY:
                    requireType(type, "Any(" + type + ") not supported.", ExpressionType.UINT);
                    code.any();
                    break;
                case CAST_TO_BOOL:
                    return ExpressionType.BOOL;
                case CAST_TO_SINT:
                    return ExpressionType.SINT;
                case CAST_TO_UINT:
                    return ExpressionType.UINT;
                default:
                    throw new AssertionError(unary.getOperator());
            }
        }
        if (expression instanceof BinaryOperationExpression) {
            BinaryOperationExpression binary = (BinaryOperationExpression) expression;
            if (binary.getOperator() == BinaryOperator.LAZY_OR) {
                UUID lazyOrId = UUID.randomUUID();
                String skip = "lazyOrSkip-" + lazyOrId;

                ExpressionType typeA = expression(binary.getA(), context);
                code.mov(Register.AX, Register.CX);
                code.conditionalJump(cx(), skip);
                ExpressionType typeB = expression(binary.getB(), context);
                code.mov(Register.AX, Register.CX);

                code.setLabel(skip);
                code.mov(Register.CX, Register.AX);
                String errorMessage = typeA + " || " + typeB + " not supported.";
                requireType(typeA, errorMessage, ExpressionType.BOOL);
                requireType(typeB, errorMessage, ExpressionType.BOOL);
                return ExpressionType.BOOL;
            }
            if (binary.getOperator() == BinaryOperator.LAZY_AND) {
                UUID lazyAndId = UUID.randomUUID();
                String skip = "lazyAndSkip-" + lazyAndId;

                ExpressionType typeA = expression(binary.getA(), context);
                code.mov(Register.AX, Register.CX);
                code.complement();
                code.conditionalJump(ax(), skip);
                ExpressionType typeB = expression(binary.getB(), context);
                code.mov(Register.AX, Register.CX);

                code.setLabel(skip);
                code.mov(Register.CX, Register.AX);
                String errorMessage = typeA + " && " + typeB + " not supported.";
                requireType(typeA, errorMessage, ExpressionType.BOOL);
                requireType(typeB, errorMessage, ExpressionType.BOOL);
                return ExpressionType.BOOL;
            }

            ExpressionType typeA = expression(binary.getA(), context);
            code.push(Register.AX);
            ExpressionType typeB = expression(binary.getB(), context);
            code.mov(Register.AX, Register.BX);
            code.pop(Register.AX);
            switch (binary.getOperator()) {
                case EQUAL:
                    code.xor(ax(), bx());
                    code.any();
                    code.complement();
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    return ExpressionType.BOOL;
                case NOT_EQUAL:
                    code.xor(ax(), bx());
                    code.any();
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    return ExpressionType.BOOL;
                case ADD:
                    code.add(ax(), bx());
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    requireType(typeA, null, ExpressionType.SINT, ExpressionType.UINT);
                    return typeA;
                case SUB:
                    code.sub(ax(), bx());
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    requireType(typeA, null, ExpressionType.SINT, ExpressionType.UINT);
                    return typeA;
                case OR:
                    code.or(ax(), bx());
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    requireType(typeA, null, ExpressionType.BOOL, ExpressionType.UINT);
                    return typeA;
                case AND:
                    code.and(ax(), bx());
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    requireType(typeA, null, ExpressionType.BOOL, ExpressionType.UINT);
                    return typeA;
                case XOR:
                    code.xor(ax(), bx());
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    requireType(typeA, null, ExpressionType.BOOL, ExpressionType.UINT);
                    return typeA;
                case LSHIFT:
                    code.leftShift(ax(), bx());
                    requireType(typeA, null, ExpressionType.UINT);
                    requireType(typeB, null, ExpressionType.UINT);
                    return ExpressionType.UINT;
                case RSHIFT:
                    code.rightShift(ax(), bx());
                    requireType(typeA, null, ExpressionType.UINT);
                    requireType(typeB, null, ExpressionType.UINT);
                    return ExpressionType.UINT;
                case LESS_OR_EQUAL:
                    code.dec();
                case LESS_THAN:
                    code.sub(ax(), bx());
                    code.mov(Register.AX, Register.BX);
                    code.signBit(Register.AX);
                    code.and(ax(), bx());
                    code.any();
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    requireType(typeA, null, ExpressionType.SINT, ExpressionType.UINT);
                    return ExpressionType.BOOL;
                case GREATER_THAN:
                    code.dec();
                case GREATER_OR_EQUAL:
                    code.sub(ax(), bx());
                    code.mov(Register.AX, Register.BX);
                    code.signBit(Register.AX);
                    code.and(ax(), bx());
                    code.any();
                    code.complement();
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    requireType(typeA, null, ExpressionType.SINT, ExpressionType.UINT);
                    return ExpressionType.BOOL;
                case MULT:
                    code.mult(ax(), bx());
                    if (typeA != typeB) {
                        throw new UnsupportedOperationException();
                    }
                    requireType(typeA, null, ExpressionType.SINT, ExpressionType.UINT);
                    return typeA;
                default:
                    throw new AssertionError(binary.getOperator());
            }
        }
        throw new UnsupportedOperationException(expression.toString());
    }

    private static void requireType(ExpressionType type, String message, ExpressionType... types) {
        if (!Arrays.asList(types).contains(type)) {
            throw new UnsupportedOperationException(message);
        }
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
