package com.etherblood.jassembly.usability.code;

/**
 *
 * @author Philipp
 */
public class ControlSignals {

    public static final Range PC_INC;
    public static final Range R0_ADR;
    public static final Range R1_ADR;
    public static final Range W_ADR;
    public static final Range OP_ADR;
    public static final Range OP_ARG;
    public static final Range R0_MOD;
    public static final Range W_MOD;
    public static final int SIGNAL_BITS;

    public static final int RAM_ADR = 0;
    public static final int ADD_ADR = 1;
    public static final int RSHIFT_ADR = 2;
    public static final int LU_ADR = 3;

    public static final int NULL_ADR = 0;
    public static final int PC_ADR = 1;
    public static final int IX_ADR = 2;
    public static final int AX_ADR = 3;
    public static final int BX_ADR = 4;
    public static final int CX_ADR = 5;
    public static final int SB_ADR = 6;
    public static final int SP_ADR = 7;

    public static final int MOD_ID = 0;
    public static final int MOD_ANY = 1;
    public static final int MOD_INV = 2;
    public static final int MOD_REV = 3;

    public static final int LU_AND = 0;
    public static final int LU_OR = 1;
    public static final int LU_XOR = 2;

    static {
        RangeBuilder builder = new RangeBuilder();
        PC_INC = builder.next(1);
        R0_ADR = builder.next(3);
        R1_ADR = builder.next(3);
        W_ADR = builder.next(3);
        OP_ADR = builder.next(2);
        OP_ARG = builder.next(2);
        R0_MOD = builder.next(2);
        W_MOD = builder.next(2);
        SIGNAL_BITS = builder.getIndex();
    }

    static long set(Range range, long value) {
        assert 1 << range.length > value;
        return value << range.offset;
    }
}
