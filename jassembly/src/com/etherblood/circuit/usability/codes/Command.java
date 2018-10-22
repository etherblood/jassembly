package com.etherblood.circuit.usability.codes;

import static com.etherblood.circuit.usability.codes.ControlSignals.*;

/**
 *
 * @author Philipp
 */
public enum Command {
    WAIT(0L),
    LOAD_CMD(set(OP_ADR, RAM_ADR) | set(R1_ADR, PC_ADR) | set(W_ADR, CMD_ADR) | set(PC_INC, 1)),
    LOAD_CONST(set(OP_ADR, RAM_ADR) | set(R1_ADR, PC_ADR) | set(W_ADR, ACC_ADR) | set(PC_INC, 1)),
    AND(set(OP_ADR, LU_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, LU_AND)),
    OR(set(OP_ADR, LU_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, LU_OR)),
    XOR(set(OP_ADR, LU_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, LU_XOR)),
    ADD_X0(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR)),
    SUB_X0(set(OP_ADR, ADD_ADR) | set(R1_ADR, ACC_ADR) | set(R0_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, 1) | set(R0_MOD, MOD_INV)),
    RSHIFT_X0(set(OP_ADR, RSHIFT_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR)),
    LSHIFT_X0(set(OP_ADR, RSHIFT_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_REV) | set(W_MOD, MOD_REV)),
    FROM_X0(set(OP_ADR, ADD_ADR) | set(R0_ADR, X0_ADR) | set(W_ADR, ACC_ADR)),
    TO_X0(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, X0_ADR)),
    FROM_X1(set(OP_ADR, ADD_ADR) | set(R0_ADR, X1_ADR) | set(W_ADR, ACC_ADR)),
    TO_X1(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, X1_ADR)),
    FROM_X2(set(OP_ADR, ADD_ADR) | set(R0_ADR, X2_ADR) | set(W_ADR, ACC_ADR)),
    TO_X2(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, X2_ADR)),
    FROM_X3(set(OP_ADR, ADD_ADR) | set(R0_ADR, X3_ADR) | set(W_ADR, ACC_ADR)),
    TO_X3(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, X3_ADR)),
    JUMP(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, PC_ADR)),
    INVERT(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_INV)),
    REVERT(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_REV)),
    ANY(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_ANY)),
    INC(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, 1)),
    DEC(set(OP_ADR, ADD_ADR) | set(R1_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_INV)),
    UNDEFINED(~0L);

    private final long controlSignals;

    private Command(long controlSignals) {
        this.controlSignals = controlSignals;
    }

    public long getControlSignals() {
        return controlSignals;
    }
}
