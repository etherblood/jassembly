package com.etherblood.jassembly.core;

public class NandGate extends BinaryGate {

    @Override
    public boolean compute(boolean a, boolean b) {
        return !(a && b);
    }

}
