package com.etherblood.jassembly.compile.jassembly.assembly;

import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.ConstantExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.JassemblyExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.LabelExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.RegisterExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.BinaryOperation;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.ConditionalJump;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.JassemblyInstruction;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.Label;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.Read;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.Terminate;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.UnaryOperation;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.Write;
import com.etherblood.jassembly.compile.jassembly.machine.Jmachine;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class JassemblyCompiler {

    public void compile(List<JassemblyInstruction> instructions, Jmachine consumer) {
        for (JassemblyInstruction instruction : instructions) {
            translateInstruction(instruction, consumer);
        }
    }

    private void translateInstruction(JassemblyInstruction instruction, Jmachine consumer) {
        if (instruction instanceof Label) {
            Label label = (Label) instruction;
            consumer.labelNext(label.getLabel());
            return;
        }
        if (instruction instanceof Terminate) {
            Terminate terminate = (Terminate) instruction;
            consumer.constant(terminate.getExitCode().ordinal(), Register.CX);
            consumer.instruction(consumer.map().terminate());
            return;
        }
        if (instruction instanceof Write) {
            Write write = (Write) instruction;
            Register from = toRegister(write.getFrom());
            Register to = toRegister(write.getTo());
            if (from == null) {
                from = findFree(to);
                resolve(write.getFrom(), from, consumer);
            }
            if (to == null) {
                to = findFree(from);
                resolve(write.getTo(), to, consumer);
            }
            consumer.instruction(consumer.map().write(from, to));
            return;
        }
        if (instruction instanceof Read) {
            Read read = (Read) instruction;
            Register from = toRegister(read.getFrom());
            Register to = read.getTo();
            if (from == null) {
                from = findFree(to);
                resolve(read.getFrom(), from, consumer);
            }
            consumer.instruction(consumer.map().read(from, to));
            return;
        }
        if (instruction instanceof ConditionalJump) {
            //TODO, AX, BX & CX should not be overwritten?
            ConditionalJump conditionalJump = (ConditionalJump) instruction;
            Register condition = toRegister(conditionalJump.getCondition());
            Register address = toRegister(conditionalJump.getJumpAddress());
            if (address == null) {
                address = findFree(condition);
                resolve(conditionalJump.getJumpAddress(), address, consumer);
            }
            consumer.instruction(consumer.map().write(address, Register.SP));
            resolve(conditionalJump.getCondition(), Register.CX, consumer);
            
            consumer.instruction(consumer.map().readConstant(Register.AX));
            consumer.instructionCode(consumer.map().jump(Register.BX));
            consumer.instruction(consumer.map().readConstant(Register.BX));
            consumer.instructionCode(consumer.map().noop());
            
            consumer.instruction(consumer.map().xor(Register.AX, Register.BX, Register.AX));
            consumer.instruction(consumer.map().and(Register.AX, Register.CX, Register.AX));
            consumer.instruction(consumer.map().xor(Register.AX, Register.BX, Register.AX));
            consumer.instruction(consumer.map().read(Register.SP, Register.BX));
            consumer.instruction(consumer.map().execute(Register.AX));
            //IX = wait ^ (condition & (wait ^ jump(jumpAddress)))
            //
            //with wait == 0 above can be optimized to
            //IX = condition & jump(jumpAddress)
            return;
        }
        if (instruction instanceof UnaryOperation) {
            UnaryOperation unary = (UnaryOperation) instruction;
            Register from = toRegister(unary.getA());
            if (from == null) {
                from = Register.AX;
                resolve(unary.getA(), from, consumer);
            }
            Register to = unary.getDest();
            switch (unary.getOperator()) {
                case ANY:
                    consumer.instruction(consumer.map().any(from, to));
                    break;
                case IDENTITY:
                    consumer.instruction(consumer.map().move(from, to));
                    break;
                case COMPLEMENT:
                    consumer.instruction(consumer.map().complement(from, to));
                    break;
                case REVERSE:
                    consumer.instruction(consumer.map().reverse(from, to));
                    break;
                case NEGATE:
                    consumer.instruction(consumer.map().negate(from, to));
                    break;
                case INC:
                    consumer.instruction(consumer.map().increment(from, to));
                    break;
                case DEC:
                    consumer.instruction(consumer.map().decrement(from, to));
                    break;
                default:
                    throw new UnsupportedOperationException(unary.getOperator().toString());
            }
            return;
        }
        if (instruction instanceof BinaryOperation) {
            BinaryOperation binary = (BinaryOperation) instruction;
            Register a = toRegister(binary.getA());
            Register b = toRegister(binary.getB());
            if (a == null) {
                a = findFree(b);
                resolve(binary.getA(), a, consumer);
            }
            if (b == null) {
                b = findFree(a);
                resolve(binary.getB(), b, consumer);
            }
            Register to = binary.getDest();
            switch(binary.getOperator()){
                case ADD:
                    consumer.instruction(consumer.map().add(a, b, to));
                    break;
                case SUB:
                    consumer.instruction(consumer.map().subtract(a, b, to));
                    break;
                case MULT:
                    throw new AssertionError(binary.getOperator().name());
                case DIV:
                    throw new AssertionError(binary.getOperator().name());
                case MOD:
                    throw new AssertionError(binary.getOperator().name());
                case AND:
                    consumer.instruction(consumer.map().and(a, b, to));
                    break;
                case OR:
                    consumer.instruction(consumer.map().or(a, b, to));
                    break;
                case XOR:
                    consumer.instruction(consumer.map().xor(a, b, to));
                    break;
                case NXOR:
                    throw new AssertionError(binary.getOperator().name());
                case EQ:
                    throw new AssertionError(binary.getOperator().name());
                case NEQ:
                    throw new AssertionError(binary.getOperator().name());
                case RSHIFT:
                    consumer.instruction(consumer.map().rightShift(a, b, to));
                    break;
                case LSHIFT:
                    consumer.instruction(consumer.map().leftShift(a, b, to));
                    break;
                default:
                    throw new AssertionError(binary.getOperator().name());
                
            }
            return;
        }
        throw new UnsupportedOperationException(instruction.toString());
    }

    private Register findFree(Register... occupied) {
        for (Register register : Arrays.asList(Register.AX, Register.BX, Register.CX)) {
            if(!Arrays.asList(occupied).contains(register)) {
                return register;
            }
        }
        throw new IllegalStateException(Arrays.toString(occupied));
    }

    private Register toRegister(JassemblyExpression expression) {
        if (expression instanceof RegisterExpression) {
            RegisterExpression registerExpression = (RegisterExpression) expression;
            return registerExpression.getRegister();
        }
        return null;
    }

    private void resolve(JassemblyExpression expression, Register register, Jmachine consumer) {
        if (expression instanceof RegisterExpression) {
            RegisterExpression registerExpression = (RegisterExpression) expression;
            consumer.instruction(consumer.map().move(registerExpression.getRegister(), register));
            return;
        }
        if (expression instanceof LabelExpression) {
            LabelExpression labelExpression = (LabelExpression) expression;
            consumer.constant(labelExpression.getLabel(), register);
            return;
        }
        if (expression instanceof ConstantExpression) {
            ConstantExpression constantExpression = (ConstantExpression) expression;
            consumer.constant(constantExpression.getValue(), register);
            return;
        }
        throw new UnsupportedOperationException(expression.toString());
    }

//    private void resolve(JassemblyExpression eA, Register rA, JassemblyExpression eB, Register rB, Jmachine consumer) {
//        if (eA instanceof RegisterExpression && eB instanceof RegisterExpression) {
//            RegisterExpression a = (RegisterExpression) eA;
//            RegisterExpression b = (RegisterExpression) eB;
//            consumer.instruction(consumer.map().write(a.getRegister(), Register.SP, Register.NONE));
//            consumer.instruction(consumer.map().move(b.getRegister(), rB));
//            consumer.instruction(consumer.map().read(Register.SP, rA));
//            return;
//        }
//        if (eB instanceof RegisterExpression) {
//            resolve(eB, rB, consumer);
//            resolve(eA, rA, consumer);
//            return;
//        }
//        resolve(eA, rA, consumer);
//        resolve(eB, rB, consumer);
//    }
}