package com.etherblood.jassembly.compile.jassembly;

import com.etherblood.jassembly.usability.codes.Command;
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

    public List<Integer> toProgram() {
        if (!pendingLabels.isEmpty()) {
            noop();
        }
        List<Integer> program = new ArrayList<>();
        for (JassemblyCommand command : commands) {
            program.add(command.toCode(commands));
        }
        return program;
    }

    public void labelNext(String label) {
        pendingLabels.add(label);
    }

    public void call(String function) {
        String returnLabel = "return-" + UUID.randomUUID().toString();
        constant(returnLabel);
        pushStack();
        constant(function);
        jump();
        labelNext(returnLabel);
    }

    public void ret() {
        popStack();
        jump();
    }

    public void noop() {
        add(simple(Command.WAIT));
    }

    public void jump() {
        add(simple(Command.JUMP));
    }

    public void toX0() {
        add(simple(Command.TO_X0));
    }

    public void toX1() {
        add(simple(Command.TO_X1));
    }

    public void toSB() {
        add(simple(Command.TO_SB));
    }

    public void toSP() {
        add(simple(Command.TO_SP));
    }

    public void fromX0() {
        add(simple(Command.FROM_X0));
    }

    public void fromX1() {
        add(simple(Command.FROM_X1));
    }

    public void fromSB() {
        add(simple(Command.FROM_SB));
    }

    public void fromSP() {
        add(simple(Command.FROM_SP));
    }

    public void add() {
        add(simple(Command.ADD));
    }

    public void sub() {
        add(simple(Command.SUB));
    }

    public void and() {
        add(simple(Command.AND));
    }

    public void or() {
        add(simple(Command.OR));
    }

    public void xor() {
        add(simple(Command.XOR));
    }

    public void lshift() {
        add(simple(Command.LSHIFT));
    }

    public void rshift() {
        add(simple(Command.RSHIFT));
    }

    public void any() {
        add(simple(Command.ANY));
    }

    public void inc() {
        add(simple(Command.INC));
    }

    public void dec() {
        add(simple(Command.DEC));
    }

    public void signBit() {
        add(simple(Command.SIGN_BIT));
    }

    public void negate() {
        add(simple(Command.INVERT));
        add(simple(Command.INC));
    }

    public void complement() {
        add(simple(Command.INVERT));
    }

    public void constant(String label) {
        add(simple(Command.LOAD_CONST));
        add(labelLiteral(label));
    }

    public void constant(int value) {
        add(simple(Command.LOAD_CONST));
        add(literal(value));
    }

    public void delStack() {
        add(simple(Command.INC_STACK));
    }

    public void popStack() {
        add(simple(Command.READ_STACK));
        add(simple(Command.INC_STACK));
    }

    public void pushStack() {
        add(simple(Command.DEC_STACK));
        add(simple(Command.WRITE_STACK));
    }

    public void readRam() {
        add(simple(Command.READ));
    }

    public void writeRam() {
        add(simple(Command.WRITE));
    }

    public void terminate() {
        add(simple(Command.TERMINATE));
    }

    public void nativeCommand(Command command) {
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

    private static JassemblyCommand simple(Command command) {
        return new SimpleCommand(command);
    }

    private static JassemblyCommand literal(int value) {
        return new LiteralCommand(value);
    }

    private static JassemblyCommand labelLiteral(String value) {
        return new LabelLiteralCommand(value);
    }
}
