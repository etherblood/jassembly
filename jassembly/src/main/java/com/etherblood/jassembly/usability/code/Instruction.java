package com.etherblood.jassembly.usability.code;

import static com.etherblood.jassembly.usability.code.ControlSignals.*;

/**
 *
 * @author Philipp
 */
public enum Instruction implements MachineInstruction {
    WAIT(0L),
    
    //load program instruction
    READ_IX_PC(set(OP_ADR, RAM_ADR) | set(R1_ADR, PC_ADR) | set(W_ADR, IX_ADR) | set(PC_INC, 1)),
    //load program constant
    READ_AX_PC(set(OP_ADR, RAM_ADR) | set(R1_ADR, PC_ADR) | set(W_ADR, AX_ADR) | set(PC_INC, 1)),
    
    READ_AX_SP(set(OP_ADR, RAM_ADR) | set(W_ADR, AX_ADR) | set(R1_ADR, SP_ADR)),
    WRITE_AX_SP(set(OP_ADR, RAM_ADR) | set(OP_ARG, 1) | set(R0_ADR, AX_ADR) | set(R1_ADR, SP_ADR)),
    READ_AX_BX(set(OP_ADR, RAM_ADR) | set(W_ADR, AX_ADR) | set(R1_ADR, BX_ADR)),
    WRITE_AX_BX(set(OP_ADR, RAM_ADR) | set(OP_ARG, 1) | set(R0_ADR, AX_ADR) | set(R1_ADR, BX_ADR)),
    
    AND_AX_BX(set(OP_ADR, LU_ADR) | set(R0_ADR, AX_ADR) | set(R1_ADR, BX_ADR) | set(W_ADR, AX_ADR) | set(OP_ARG, LU_AND)),
    OR_AX_BX(set(OP_ADR, LU_ADR) | set(R0_ADR, AX_ADR) | set(R1_ADR, BX_ADR) | set(W_ADR, AX_ADR) | set(OP_ARG, LU_OR)),
    XOR_AX_BX(set(OP_ADR, LU_ADR) | set(R0_ADR, AX_ADR) | set(R1_ADR, BX_ADR) | set(W_ADR, AX_ADR) | set(OP_ARG, LU_XOR)),
    ADD_AX_BX(set(OP_ADR, ADD_ADR) | set(R0_ADR, AX_ADR) | set(R1_ADR, BX_ADR) | set(W_ADR, AX_ADR)),
    SUB_AX_BX(set(OP_ADR, ADD_ADR) | set(R1_ADR, AX_ADR) | set(R0_ADR, BX_ADR) | set(W_ADR, AX_ADR) | set(OP_ARG, 1) | set(R0_MOD, MOD_INV)),
    RSHIFT_AX_BX(set(OP_ADR, RSHIFT_ADR) | set(R0_ADR, AX_ADR) | set(R1_ADR, BX_ADR) | set(W_ADR, AX_ADR)),
    LSHIFT_AX_BX(set(OP_ADR, RSHIFT_ADR) | set(R0_ADR, AX_ADR) | set(R1_ADR, BX_ADR) | set(W_ADR, AX_ADR) | set(R0_MOD, MOD_REV) | set(W_MOD, MOD_REV)),
    
    MOV_BX_AX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, BX_ADR) | set(W_ADR, AX_ADR)),
    MOV_AX_BX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, AX_ADR) | set(W_ADR, BX_ADR)),
    MOV_CX_AX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, CX_ADR) | set(W_ADR, AX_ADR)),
    MOV_AX_CX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, AX_ADR) | set(W_ADR, CX_ADR)),
    MOV_BX_CX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, BX_ADR) | set(W_ADR, CX_ADR)),
    MOV_CX_BX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, CX_ADR) | set(W_ADR, BX_ADR)),
    
    MOV_SB_AX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, SB_ADR) | set(W_ADR, AX_ADR)),
    MOV_AX_SB(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, AX_ADR) | set(W_ADR, SB_ADR)),
    MOV_SP_AX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, SP_ADR) | set(W_ADR, AX_ADR)),
    MOV_AX_SP(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, AX_ADR) | set(W_ADR, SP_ADR)),
    //jump
    MOV_AX_PC(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, AX_ADR) | set(W_ADR, PC_ADR)),
    
    COMPLEMENT_AX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, AX_ADR) | set(W_ADR, AX_ADR) | set(W_MOD, MOD_INV)),
    REVERT_AX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, AX_ADR) | set(W_ADR, AX_ADR) | set(W_MOD, MOD_REV)),
    ANY_AX(set(OP_ADR, LU_ADR) | set(OP_ARG, LU_OR) | set(R0_ADR, AX_ADR) | set(W_ADR, AX_ADR) | set(W_MOD, MOD_ANY)),
    INC_AX(set(OP_ADR, ADD_ADR) | set(R0_ADR, AX_ADR) | set(W_ADR, AX_ADR) | set(OP_ARG, 1)),
    DEC_AX(set(OP_ADR, ADD_ADR) | set(R1_ADR, AX_ADR) | set(W_ADR, AX_ADR) | set(R0_MOD, MOD_INV)),
    INC_SP(set(OP_ADR, ADD_ADR) | set(OP_ARG, 1) | set(R0_ADR, SP_ADR) | set(W_ADR, SP_ADR)),
    DEC_SP(set(OP_ADR, ADD_ADR) | set(R1_ADR, SP_ADR) | set(W_ADR, SP_ADR) | set(R0_MOD, MOD_INV)),
    
    SIGN_BIT(set(OP_ADR, ADD_ADR) | set(W_ADR, AX_ADR) | set(OP_ARG, 1) | set(W_MOD, MOD_REV)),
    TERMINATE(~0L);

    private final long controlFlags;

    private Instruction(long controlFlags) {
        this.controlFlags = controlFlags;
    }

    @Override
    public long getControlFlags() {
        return controlFlags;
    }
}
