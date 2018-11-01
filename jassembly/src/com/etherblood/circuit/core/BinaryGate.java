package com.etherblood.circuit.core;

public abstract class BinaryGate {

    private Wire a, b;
    private final Wire out;

    public BinaryGate() {
        this(Wire.off(), Wire.off());
    }

    public BinaryGate(Wire a, Wire b) {
        this(a, b, Wire.off());
    }

    public BinaryGate(Wire a, Wire b, Wire out) {
        this.a = a;
        this.b = b;
        this.out = out;
        a.attachChild(this);
        if (a != b) {
            b.attachChild(this);
        }
    }

    boolean compute() {
        return compute(a.getSignal(), b.getSignal());
    }

    abstract boolean compute(boolean a, boolean b);

    public void setA(Wire wire) {
        if (a != b) {
            a.detachChild(this);
        }
        a = wire;
        if (a != b) {
            a.attachChild(this);
        }
    }

    public void setB(Wire wire) {
        if (a != b) {
            b.detachChild(this);
        }
        b = wire;
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
