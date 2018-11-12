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

    private final List<Labelled> commands = new ArrayList<>();
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
        add(simple(Instruction.MOV_AX_PC));
    }

    public void toX0() {
        add(simple(Instruction.MOV_AX_BX));
    }

    public void toX1() {
        add(simple(Instruction.MOV_AX_CX));
    }

    public void toSB() {
        add(simple(Instruction.MOV_AX_SB));
    }

    public void toSP() {
        add(simple(Instruction.MOV_AX_SP));
    }

    public void fromX0() {
        add(simple(Instruction.MOV_BX_AX));
    }

    public void fromX1() {
        add(simple(Instruction.MOV_CX_AX));
    }

    public void fromSB() {
        add(simple(Instruction.MOV_SB_AX));
    }

    public void fromSP() {
        add(simple(Instruction.MOV_SP_AX));
    }

    public void add() {
        add(simple(Instruction.ADD_AX_BX));
    }

    public void sub() {
        add(simple(Instruction.SUB_AX_BX));
    }

    public void and() {
        add(simple(Instruction.AND_AX_BX));
    }

    public void or() {
        add(simple(Instruction.OR_AX_BX));
    }

    public void xor() {
        add(simple(Instruction.XOR_AX_BX));
    }

    public void lshift() {
        add(simple(Instruction.LSHIFT_AX_BX));
    }

    public void rshift() {
        add(simple(Instruction.RSHIFT_AX_BX));
    }

    public void any() {
        add(simple(Instruction.ANY_AX));
    }

    public void inc() {
        add(simple(Instruction.INC_AX));
    }

    public void dec() {
        add(simple(Instruction.DEC_AX));
    }

    public void signBit() {
        add(simple(Instruction.SIGN_BIT));
    }

    public void negate() {
        add(simple(Instruction.INVERT_AX));
        add(simple(Instruction.INC_AX));
    }

    public void complement() {
        add(simple(Instruction.INVERT_AX));
    }

    public void constant(String label) {
        add(simple(Instruction.READ_AX_PC));
        add(labelLiteral(label));
    }

    public void constant(int value) {
        add(simple(Instruction.READ_AX_PC));
        add(literal(value));
    }

    public void stackDel() {
        add(simple(Instruction.INC_SP));
    }

    public void stackPop() {
        add(simple(Instruction.READ_AX_SP));
        add(simple(Instruction.INC_SP));
    }

    public void stackPush() {
        add(simple(Instruction.DEC_SP));
        add(simple(Instruction.WRITE_AX_SP));
    }

    public void ramRead() {
        add(simple(Instruction.READ_AX_BX));
    }

    public void ramWrite() {
        add(simple(Instruction.WRITE_AX_BX));
    }

    public void terminate() {
        add(simple(Instruction.TERMINATE));
    }

    public void nativeCommand(Instruction command) {
        add(simple(command));
    }

    public List<Labelled> getCommands() {
        return commands;
    }

    private void add(Labelled command) {
        command.getLabels().addAll(pendingLabels);
        pendingLabels.clear();
        commands.add(command);
    }

    private static Labelled simple(Instruction command) {
        return new LabelledInstruction(command);
    }

    private static Labelled literal(int value) {
        return new LabelledLiteral(value);
    }

    private static Labelled labelLiteral(String value) {
        return new LabelledLabel(value);
    }
}
