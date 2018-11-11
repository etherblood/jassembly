package com.etherblood.jassembly.compile.jassembly;

import com.etherblood.jassembly.usability.code.Instruction;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Philipp
 */
public class Jassembly {

    private final List<JassemblyCommand> commands = new ArrayList<>();
    private final List<String> pendingLabels = new ArrayList<>();

    public void labelNext(String label) {
        pendingLabels.add(label);
    }

    public void call(String function) {
        String returnLabel = "return-" + UUID.randomUUID().toString();
        constant(returnLabel);
        stackPush();
        constant(function);
        jump();
        labelNext(returnLabel);
    }

    public void ret() {
        stackPop();
        jump();
    }

    public void noop() {
        add(simple(Instruction.WAIT));
    }

    public void jump() {
        add(simple(Instruction.JUMP));
    }

    public void toX0() {
        add(simple(Instruction.TO_X0));
    }

    public void toX1() {
        add(simple(Instruction.TO_X1));
    }

    public void toSB() {
        add(simple(Instruction.TO_SB));
    }

    public void toSP() {
        add(simple(Instruction.TO_SP));
    }

    public void fromX0() {
        add(simple(Instruction.FROM_X0));
    }

    public void fromX1() {
        add(simple(Instruction.FROM_X1));
    }

    public void fromSB() {
        add(simple(Instruction.FROM_SB));
    }

    public void fromSP() {
        add(simple(Instruction.FROM_SP));
    }

    public void add() {
        add(simple(Instruction.ADD));
    }

    public void sub() {
        add(simple(Instruction.SUB));
    }

    public void and() {
        add(simple(Instruction.AND));
    }

    public void or() {
        add(simple(Instruction.OR));
    }

    public void xor() {
        add(simple(Instruction.XOR));
    }

    public void lshift() {
        add(simple(Instruction.LSHIFT));
    }

    public void rshift() {
        add(simple(Instruction.RSHIFT));
    }

    public void any() {
        add(simple(Instruction.ANY));
    }

    public void inc() {
        add(simple(Instruction.INC));
    }

    public void dec() {
        add(simple(Instruction.DEC));
    }

    public void signBit() {
        add(simple(Instruction.SIGN_BIT));
    }

    public void negate() {
        add(simple(Instruction.INVERT));
        add(simple(Instruction.INC));
    }

    public void complement() {
        add(simple(Instruction.INVERT));
    }

    public void constant(String label) {
        add(simple(Instruction.LOAD_CONST));
        add(labelLiteral(label));
    }

    public void constant(int value) {
        add(simple(Instruction.LOAD_CONST));
        add(literal(value));
    }

    public void stackDel() {
        add(simple(Instruction.INC_STACK));
    }

    public void stackPop() {
        add(simple(Instruction.READ_STACK));
        add(simple(Instruction.INC_STACK));
    }

    public void stackPush() {
        add(simple(Instruction.DEC_STACK));
        add(simple(Instruction.WRITE_STACK));
    }

    public void ramRead() {
        add(simple(Instruction.READ));
    }

    public void ramWrite() {
        add(simple(Instruction.WRITE));
    }

    public void terminate() {
        add(simple(Instruction.TERMINATE));
    }

    public void nativeCommand(Instruction command) {
        add(simple(command));
    }

    public List<JassemblyCommand> getCommands() {
        return commands;
    }

    private void add(JassemblyCommand command) {
        command.getLabels().addAll(pendingLabels);
        pendingLabels.clear();
        commands.add(command);
    }

    private static JassemblyCommand simple(Instruction command) {
        return new SimpleCommand(command);
    }

    private static JassemblyCommand literal(int value) {
        return new LiteralCommand(value);
    }

    private static JassemblyCommand labelLiteral(String value) {
        return new LabelLiteralCommand(value);
    }
}
