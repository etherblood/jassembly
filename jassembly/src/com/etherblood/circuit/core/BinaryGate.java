package com.etherblood.circuit.core;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class BinaryGate {

    private static final AtomicInteger ID_SUPPLY = new AtomicInteger(0);
    private final int id = ID_SUPPLY.getAndIncrement();
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
        out.setParent(this);
    }

    public void compute() {
        out.scheduleSignal(compute(a.getSignal(), b.getSignal()));
    }

    public abstract boolean compute(boolean a, boolean b);

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

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

}
