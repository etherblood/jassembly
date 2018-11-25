package com.etherblood.jassembly.usability.code;

import com.etherblood.jassembly.compile.jassembly.Register;
import static com.etherblood.jassembly.usability.code.ControlSignals.*;

/**
 *
 * @author Philipp
 */
public class MachineInstructions implements InstructionMapping {

    @Override
    public MachineInstruction identity(Register a, Register c) {
        return new SimpleMachineInstruction("identity " + unaryString(a, c), moveFlags(a, c));
    }

    @Override
    public MachineInstruction any(Register a, Register c) {
        return new SimpleMachineInstruction("any " + unaryString(a, c), moveFlags(a, c) | set(W_MOD, MOD_ANY));
    }

    @Override
    public MachineInstruction complement(Register a, Register c) {
        return new SimpleMachineInstruction("complement " + unaryString(a, c), moveFlags(a, c) | set(W_MOD, MOD_INV));
    }

    @Override
    public MachineInstruction reverse(Register a, Register c) {
        return new SimpleMachineInstruction("reverse " + unaryString(a, c), moveFlags(a, c) | set(W_MOD, MOD_REV));
    }

    @Override
    public MachineInstruction negate(Register a, Register c) {
        return new SimpleMachineInstruction("negate " + unaryString(a, c), subtractFlags(Register.NONE, a, c));
    }

    @Override
    public MachineInstruction increment(Register a, Register c) {
        return new SimpleMachineInstruction("increment " + unaryString(a, c), incrementFlags(a, c));
    }

    private long incrementFlags(Register a, Register c) {
        return addFlags(a, Register.NONE, c) | set(OP_ARG, 1);
    }

    @Override
    public MachineInstruction decrement(Register a, Register c) {
        return new SimpleMachineInstruction("decrement " + unaryString(a, c), decrementFlags(a, c));
    }

    private long decrementFlags(Register a, Register c) {
        return addFlags(Register.NONE, a, c) | set(R0_MOD, MOD_INV);
    }

    @Override
    public MachineInstruction leftShift(Register a, Register b, Register c) {
        return new SimpleMachineInstruction("lshift " + binaryString(a, b, c), rightShiftFlags(a, b, c) | set(R0_MOD, MOD_REV) | set(W_MOD, MOD_REV));
    }

    @Override
    public MachineInstruction rightShift(Register a, Register b, Register c) {
        return new SimpleMachineInstruction("rshift " + binaryString(a, b, c), rightShiftFlags(a, b, c));
    }

    private long rightShiftFlags(Register a, Register b, Register c) {
        return binaryFlags(a, b, c) | set(OP_ADR, RSHIFT_ADR);
    }

    @Override
    public MachineInstruction and(Register a, Register b, Register c) {
        return new SimpleMachineInstruction("and " + binaryString(a, b, c), binaryFlags(a, b, c) | set(OP_ADR, LU_ADR) | set(OP_ARG, LU_AND));
    }

    @Override
    public MachineInstruction or(Register a, Register b, Register c) {
        return new SimpleMachineInstruction("or " + binaryString(a, b, c), orFlags(a, b, c));
    }

    private long orFlags(Register a, Register b, Register c) {
        return binaryFlags(a, b, c) | set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR);
    }

    @Override
    public MachineInstruction xor(Register a, Register b, Register c) {
        return new SimpleMachineInstruction("xor " + binaryString(a, b, c), binaryFlags(a, b, c) | set(OP_ADR, LU_ADR) | set(OP_ARG, LU_XOR));
    }

    @Override
    public MachineInstruction subtract(Register a, Register b, Register c) {
        return new SimpleMachineInstruction("sub " + binaryString(a, b, c), subtractFlags(b, a, c));
    }

    private long subtractFlags(Register b, Register a, Register c) {
        return addFlags(b, a, c) | set(OP_ARG, 1) | set(R0_MOD, MOD_INV);
    }

    @Override
    public MachineInstruction add(Register a, Register b, Register c) {
        return new SimpleMachineInstruction("add " + binaryString(a, b, c), addFlags(a, b, c));
    }

    private long addFlags(Register a, Register b, Register c) {
        return binaryFlags(a, b, c) | set(OP_ADR, ADD_ADR);
    }

    @Override
    public MachineInstruction execute(Register a) {
        return new SimpleMachineInstruction("execute " + registerString(a), moveFlags(a, Register.IX));
    }

    @Override
    public MachineInstruction jump(Register a) {
        return new SimpleMachineInstruction("jump " + registerString(a), moveFlags(a, Register.PC));
    }

    @Override
    public MachineInstruction noop() {
        return new SimpleMachineInstruction("wait", constantZeroFlags(Register.NONE));
    }

    @Override
    public MachineInstruction terminate() {
        return new SimpleMachineInstruction("terminate", moveFlags(Register.PC, Register.PC));
    }

    @Override
    public MachineInstruction move(Register a, Register c) {
        return new SimpleMachineInstruction("move " + unaryString(a, c), moveFlags(a, c));
    }

    private long moveFlags(Register a, Register c) {
        return orFlags(a, Register.NONE, c);
    }

    @Override
    public MachineInstruction readInstruction() {
        return new SimpleMachineInstruction("read instruction", readConstantFlags(Register.IX));
    }

    @Override
    public MachineInstruction readConstant(Register c) {
        return new SimpleMachineInstruction("read constant " + nonaryString(c), readConstantFlags(c));
    }

    private long readConstantFlags(Register c) {
        return readFlags(Register.PC, c) | set(PC_INC, 1);
    }

    @Override
    public MachineInstruction read(Register a, Register c) {
        return new SimpleMachineInstruction("read $" + a.name() + " -> " + c.name(), readFlags(a, c));
    }

    private long readFlags(Register a, Register c) {
        return binaryFlags(Register.NONE, a, c) | set(OP_ADR, RAM_ADR);
    }

    @Override
    public MachineInstruction write(Register a, Register b) {
        return new SimpleMachineInstruction("write " + a.name() + " -> $" + b.name(), binaryFlags(a, b, Register.NONE) | set(OP_ADR, RAM_ADR) | set(OP_ARG, 1));
    }

    @Override
    public MachineInstruction constantFalse(Register c) {
        return new SimpleMachineInstruction("constant false " + nonaryString(c), constantZeroFlags(c));
    }

    @Override
    public MachineInstruction constantTrue(Register c) {
        return new SimpleMachineInstruction("constant true " + nonaryString(c), constantNegativeOneFlags(c));
    }

    @Override
    public MachineInstruction constantZero(Register c) {
        return new SimpleMachineInstruction("constant 0 " + nonaryString(c), constantZeroFlags(c));
    }

    private long constantZeroFlags(Register c) {
        return moveFlags(Register.NONE, c);
    }

    @Override
    public MachineInstruction constantOne(Register c) {
        return new SimpleMachineInstruction("constant 1 " + nonaryString(c), constantOneFlags(c));
    }

    private long constantOneFlags(Register c) {
        return incrementFlags(Register.NONE, c);
    }

    @Override
    public MachineInstruction constantNegativeOne(Register c) {
        return new SimpleMachineInstruction("constant -1 " + nonaryString(c), constantNegativeOneFlags(c));
    }

    private long constantNegativeOneFlags(Register c) {
        return decrementFlags(Register.NONE, c);
    }

    @Override
    public MachineInstruction constantSignBit(Register c) {
        return new SimpleMachineInstruction("constant sign-bit " + nonaryString(c), constantOneFlags(c) | set(W_MOD, MOD_REV));
    }

    private String registerString(Register c) {
        return c.name();
    }

    private String nonaryString(Register c) {
        return "-> " + c.name();
    }

    private String unaryString(Register a, Register c) {
        return a.name() + " -> " + c.name();
    }

    private String binaryString(Register a, Register b, Register c) {
        return "(" + a.name() + ", " + b.name() + ") -> " + c.name();
    }

    private long binaryFlags(Register a, Register b, Register c) {
        return set(R0_ADR, address(a)) | set(R1_ADR, address(b)) | set(W_ADR, address(c));
    }

    private int address(Register register) {
        switch (register) {
            case NONE:
                return NULL_ADR;
            case AX:
                return AX_ADR;
            case BX:
                return BX_ADR;
            case CX:
                return CX_ADR;
            case PC:
                return PC_ADR;
            case IX:
                return IX_ADR;
            case SP:
                return SP_ADR;
            case SB:
                return SB_ADR;
            default:
                throw new AssertionError(register.name());

        }
    }
}
