package com.etherblood.circuit.core;

import com.etherblood.circuit.usability.signals.HasSignal;
import java.util.Arrays;

/**
 *
 * @author Philipp
 */
public class Wire implements HasSignal {

    private static final int MAX_CHILDS = 256;
    private boolean signal;
    private boolean scheduledSignal;
    private BinaryGate parent;
    private BinaryGate[] childs = {};

    Wire(boolean signal) {
        this(null, signal);
    }

    Wire(BinaryGate parent, boolean signal) {
        this.parent = parent;
        this.signal = signal;
        this.scheduledSignal = signal;
    }

    @Override
    public boolean getSignal() {
        return signal;
    }

    @Override
    public void setSignal(boolean signal) {
        this.signal = signal;
    }

    public void scheduleSignal(boolean signal) {
        scheduledSignal = signal;
    }

    BinaryGate[] childs() {
        return childs;
    }

    BinaryGate parent() {
        return parent;
    }

    void setParent(BinaryGate parent) {
        this.parent = parent;
    }

    boolean requiresUpdate() {
        return signal != scheduledSignal;
    }

    void update() {
        signal = scheduledSignal;
    }

    void attachChild(BinaryGate child) {
        if (childs.length >= MAX_CHILDS) {
            throw new UnsupportedOperationException("Gate cannot have more than " + MAX_CHILDS + " children.");
        }
        addChild(child);
    }

    private void addChild(BinaryGate child) {
        assert !Arrays.asList(childs).contains(child);
        BinaryGate[] newChilds = new BinaryGate[childs.length + 1];
        System.arraycopy(childs, 0, newChilds, 0, childs.length);
        newChilds[childs.length] = child;
        childs = newChilds;
    }

    void detachChild(BinaryGate child) {
        removeChildAt(Arrays.asList(childs).indexOf(child));
    }

    private void removeChildAt(int index) {
        BinaryGate[] newChilds = new BinaryGate[childs.length - 1];
        System.arraycopy(childs, 0, newChilds, 0, index);
        System.arraycopy(childs, index + 1, newChilds, index, newChilds.length - index);
        childs = newChilds;
    }

    public static Wire on() {
        return new Wire(true);
    }

    public static Wire off() {
        return new Wire(false);
    }

    public static Wire instance(boolean signal) {
        return new Wire(signal);
    }
    
    public static Wire merge(Wire source, Wire target) {
        for (BinaryGate child : target.childs()) {
            if(child.getA() == target) {
                child.setA(source);
            }
            if(child.getB() == target) {
                child.setB(source);
            }
        }
        return source;
    }
}
