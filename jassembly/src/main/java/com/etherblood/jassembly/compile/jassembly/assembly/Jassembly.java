package com.etherblood.jassembly.compile.jassembly.assembly;

import com.etherblood.jassembly.compile.jassembly.UnaryOperator;
import com.etherblood.jassembly.compile.jassembly.BinaryOperator;
import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.BinaryExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.UnaryOperation;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.JassemblyInstruction;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.ConstantExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.RegisterExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.JassemblyExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.LabelExpression;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.BinaryOperation;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.ConditionalJump;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.ExitCode;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.Label;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.Read;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.Terminate;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.Write;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Philipp
 */
public class Jassembly {

    private final List<JassemblyInstruction> instructions = new ArrayList<>();

    public void setLabel(String label) {
        add(new Label(label));
    }

    public void terminate(ExitCode exitCode) {
        add(new Terminate(exitCode));
    }

    public void call(String function) {
        //TODO: should be a single instruction?
        String returnLabel = "return-" + UUID.randomUUID().toString();
        push(label(returnLabel));
        jump(function);
        setLabel(returnLabel);
    }

    public void ret() {
        pop(Register.PC);
    }
    
    public void conditionalJump(JassemblyExpression condition, JassemblyExpression jumpAddress) {
        add(new ConditionalJump(condition, jumpAddress));
    }

    public void any() {
        unary(register(Register.AX), UnaryOperator.ANY, Register.AX);
    }

    public void complement() {
        unary(register(Register.AX), UnaryOperator.COMPLEMENT, Register.AX);
    }

    public void negate() {
        unary(register(Register.AX), UnaryOperator.NEGATE, Register.AX);
    }

    public void reverse() {
        unary(register(Register.AX), UnaryOperator.REVERSE, Register.AX);
    }

    public void inc() {
        inc(Register.AX);
    }

    public void dec() {
        dec(Register.AX);
    }

    public void inc(Register register) {
        inc(register(register), register);
    }

    public void inc(JassemblyExpression value, Register to) {
        unary(value, UnaryOperator.INC, to);
    }

    public void dec(Register register) {
        dec(register(register), register);
    }

    public void dec(JassemblyExpression value, Register to) {
        unary(value, UnaryOperator.DEC, to);
    }

    public void push(Register register) {
        push(register(register));
    }

    public void push(JassemblyExpression value) {
        write(value, register(Register.SP));
        dec(Register.SP);
    }

    public void pop(Register to) {
        inc(Register.SP);
        read(register(Register.SP), to);
    }

    public void unary(JassemblyExpression value, UnaryOperator operator, Register to) {
        add(new UnaryOperation(value, operator, to));
    }

    public void add(JassemblyExpression a, JassemblyExpression b) {
        binary(a, b, BinaryOperator.ADD, Register.AX);
    }

    public void sub(JassemblyExpression a, JassemblyExpression b) {
        binary(a, b, BinaryOperator.SUB, Register.AX);
    }

    public void and(JassemblyExpression a, JassemblyExpression b) {
        binary(a, b, BinaryOperator.AND, Register.AX);
    }

    public void or(JassemblyExpression a, JassemblyExpression b) {
        binary(a, b, BinaryOperator.OR, Register.AX);
    }

    public void xor(JassemblyExpression a, JassemblyExpression b) {
        binary(a, b, BinaryOperator.XOR, Register.AX);
    }

    public void lshift(JassemblyExpression a, JassemblyExpression b) {
        binary(a, b, BinaryOperator.LSHIFT, Register.AX);
    }

    public void rshift(JassemblyExpression a, JassemblyExpression b) {
        binary(a, b, BinaryOperator.RSHIFT, Register.AX);
    }

    public void binary(JassemblyExpression a, JassemblyExpression b, BinaryOperator operator, Register to) {
        add(new BinaryOperation(a, b, operator, to));
    }

    public void read(JassemblyExpression readAddress, Register to) {
        add(new Read(readAddress, to));
    }

    public void write(JassemblyExpression value, JassemblyExpression writeAddress) {
        add(new Write(value, writeAddress));
    }

    public void jump(String label) {
        jump(label(label));
    }

    public void jump(JassemblyExpression expression) {
        mov(expression, Register.PC);
    }

    public void mov(Register from, Register to) {
        mov(register(from), to);
    }

    public void mov(int value, Register to) {
        mov(constant(value), to);
    }

    public void mov(String label, Register to) {
        mov(label(label), to);
    }

    public void mov(JassemblyExpression expression, Register to) {
        add(new UnaryOperation(expression, UnaryOperator.IDENTITY, to));
    }

    public void signBit(Register to) {
        mov(constant(1), to);
        unary(register(to), UnaryOperator.REVERSE, to);
    }

    private JassemblyExpression register(Register from) {
        return new RegisterExpression(from);
    }

    private JassemblyExpression constant(int value) {
        return new ConstantExpression(value);
    }

    private JassemblyExpression label(String label) {
        return new LabelExpression(label);
    }

    private void add(JassemblyInstruction instruction) {
        instructions.add(instruction);
    }

    public List<JassemblyInstruction> getInstructions() {
        return instructions;
    }
}
