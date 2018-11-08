package com.etherblood.circuit.compile.jassembly;

import com.etherblood.circuit.usability.codes.Command;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class Jassembly {

    private final List<JassemblyCommand> commands = new ArrayList<>();
    private final List<String> pendingLabels = new ArrayList<>();

    public List<Integer> toProgram() {
        List<Integer> program = new ArrayList<>();
        for (JassemblyCommand command : commands) {
            program.add(command.toCode(commands));
        }
        return program;
    }

    public void labelNext(String label) {
        pendingLabels.add(label);
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

    public void toX2() {
        add(simple(Command.TO_X2));
    }

    public void fromX0() {
        add(simple(Command.FROM_X0));
    }

    public void fromX1() {
        add(simple(Command.FROM_X1));
    }

    public void fromX2() {
        add(simple(Command.FROM_X2));
    }

    public void add() {
        add(simple(Command.ADD));
    }

    public void sub() {
        add(simple(Command.SUB));
    }

    public void inc() {
        add(simple(Command.INC));
    }

    public void dec() {
        add(simple(Command.DEC));
    }

    public void negate() {
        add(simple(Command.INVERT));
        add(simple(Command.INC));
    }

    public void complement() {
        add(simple(Command.INVERT));
    }

    public void constant(String label, int offset) {
        add(simple(Command.LOAD_CONST));
        add(labelLiteral(label, offset));
    }

    public void constant(int value) {
        add(simple(Command.LOAD_CONST));
        add(literal(value));
    }

    public void popStack() {
        add(simple(Command.READ_STACK));
        add(simple(Command.INC_STACK));
    }

    public void pushStack() {
        add(simple(Command.DEC_STACK));
        add(simple(Command.WRITE_STACK));
    }

    public void terminate() {
        add(simple(Command.TERMINATE));
    }

    public void nativeCommand(Command command) {
        add(simple(command));
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

    private static JassemblyCommand labelLiteral(String value, int offset) {
        return new LabelLiteralCommand(value, offset);
    }
}
