package com.etherblood.jassembly.core;

import java.util.Objects;

public abstract class BinaryGate {

    private Wire a = Wire.off(), b = Wire.off();
    private final Wire out = Wire.mutable(false);

    boolean compute() {
        return compute(a.getSignal(), b.getSignal());
    }

    abstract boolean compute(boolean a, boolean b);

    public void setA(Wire wire) {
        if (a != b) {
            a.detachChild(this);
        }
        a = Objects.requireNonNull(wire);
        if (a != b) {
            a.attachChild(this);
        }
    }

    public void setB(Wire wire) {
        if (a != b) {
            b.detachChild(this);
        }
        b = Objects.requireNonNull(wire);
        if (a != b) {
            b.attachChild(this);
        }
    }

    public Wire getA() {
        return a;
    }

    public Wire getB() {
        return b;
    }

    public Wire getOut() {
        return out;
    }

}
