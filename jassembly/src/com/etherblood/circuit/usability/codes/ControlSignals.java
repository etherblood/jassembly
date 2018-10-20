package com.etherblood.circuit.usability.codes;

/**
 *
 * @author Philipp
 */
public class ControlSignals {

    public static final Range PC_INC = new Range(0, 1);
    public static final Range R0_ADR = new Range(1, 3);
    public static final Range R1_ADR = new Range(4, 3);
    public static final Range W_ADR = new Range(7, 3);
    public static final Range OP_ADR = new Range(10, 1);
    public static final Range OP_ARG = new Range(11, 1);
    public static final Range R0_MOD = new Range(12, 2);
    public static final Range W_MOD = new Range(14, 2);
    public static final int SIGNAL_BITS = 16;

    public static final int RAM_ADR = 0;
    public static final int ADD_ADR = 1;

    public static final int NULL_ADR = 0;
    public static final int PC_ADR = 1;
    public static final int CMD_ADR = 2;
    public static final int ACC_ADR = 3;
    public static final int X0_ADR = 4;
    public static final int X1_ADR = 5;
    public static final int X2_ADR = 6;
    public static final int X3_ADR = 7;
    
    public static final int MOD_ID = 0;
    public static final int MOD_ANY = 1;
    public static final int MOD_INV = 2;
    public static final int MOD_REV = 3;

    static long set(Range range, long value) {
        assert 1 << range.length > value;
        return value << range.offset;
    }
}
