package com.etherblood.jassembly.usability.code;

import static com.etherblood.jassembly.usability.code.ControlSignals.*;

/**
 *
 * @author Philipp
 */
public enum Instruction {
    WAIT(0L),
    LOAD_CMD(set(OP_ADR, RAM_ADR) | set(R1_ADR, PC_ADR) | set(W_ADR, CMD_ADR) | set(PC_INC, 1)),
    LOAD_CONST(set(OP_ADR, RAM_ADR) | set(R1_ADR, PC_ADR) | set(W_ADR, ACC_ADR) | set(PC_INC, 1)),
    AND(set(OP_ADR, LU_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, LU_AND)),
    OR(set(OP_ADR, LU_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, LU_OR)),
    XOR(set(OP_ADR, LU_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, LU_XOR)),
    ADD(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR)),
    SUB(set(OP_ADR, ADD_ADR) | set(R1_ADR, ACC_ADR) | set(R0_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, 1) | set(R0_MOD, MOD_INV)),
    RSHIFT(set(OP_ADR, RSHIFT_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR)),
    LSHIFT(set(OP_ADR, RSHIFT_ADR) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_REV) | set(W_MOD, MOD_REV)),
    FROM_X0(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, X0_ADR) | set(W_ADR, ACC_ADR)),
    TO_X0(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, ACC_ADR) | set(W_ADR, X0_ADR)),
    FROM_X1(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, X1_ADR) | set(W_ADR, ACC_ADR)),
    TO_X1(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, ACC_ADR) | set(W_ADR, X1_ADR)),
    FROM_SB(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, SB_ADR) | set(W_ADR, ACC_ADR)),
    TO_SB(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, ACC_ADR) | set(W_ADR, SB_ADR)),
    FROM_SP(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, SP_ADR) | set(W_ADR, ACC_ADR)),
    TO_SP(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, ACC_ADR) | set(W_ADR, SP_ADR)),
    JUMP(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, ACC_ADR) | set(W_ADR, PC_ADR)),
    INVERT(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_INV)),
    REVERT(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_REV)),
    ANY(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_ANY)),
    INC(set(OP_ADR, ADD_ADR) | set(R0_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, 1)),
    DEC(set(OP_ADR, ADD_ADR) | set(R1_ADR, ACC_ADR) | set(W_ADR, ACC_ADR) | set(R0_MOD, MOD_INV)),
    SIGN_BIT(set(OP_ADR, ADD_ADR) | set(W_ADR, ACC_ADR) | set(OP_ARG, 1) | set(W_MOD, MOD_REV)),
    READ_STACK(set(OP_ADR, RAM_ADR) | set(W_ADR, ACC_ADR) | set(R1_ADR, SP_ADR)),
    WRITE_STACK(set(OP_ADR, RAM_ADR) | set(OP_ARG, 1) | set(R0_ADR, ACC_ADR) | set(R1_ADR, SP_ADR)),
    READ(set(OP_ADR, RAM_ADR) | set(W_ADR, ACC_ADR) | set(R1_ADR, X0_ADR)),
    WRITE(set(OP_ADR, RAM_ADR) | set(OP_ARG, 1) | set(R0_ADR, ACC_ADR) | set(R1_ADR, X0_ADR)),
    INC_STACK(set(OP_ADR, ADD_ADR) | set(OP_ARG, 1) | set(R0_ADR, SP_ADR) | set(W_ADR, SP_ADR)),
    DEC_STACK(set(OP_ADR, ADD_ADR) | set(R1_ADR, SP_ADR) | set(W_ADR, SP_ADR) | set(R0_MOD, MOD_INV)),
    TERMINATE(~0L),
    UNDEFINED(~0L);

    private final long controlSignals;

    private Instruction(long controlSignals) {
        this.controlSignals = controlSignals;
    }

    public long getControlSignals() {
        return controlSignals;
    }
}
