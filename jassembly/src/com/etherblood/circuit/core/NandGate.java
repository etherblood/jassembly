package com.etherblood.circuit.core;

public class NandGate extends BinaryGate {

    @Override
    public boolean compute(boolean a, boolean b) {
        return !(a && b);
    }

}
