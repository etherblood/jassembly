package com.etherblood.circuit.core;

import com.etherblood.circuit.usability.signals.HasSignal;
import java.util.Arrays;

/**
 *
 * @author Philipp
 */
public class Wire implements HasSignal {

    public static final int MAX_CHILDS = 256;
    private static final BinaryGate[] EMPTY = {};
    private boolean signal;
    private BinaryGate[] childs = EMPTY;

    Wire(boolean signal) {
        this.signal = signal;
    }

    @Override
    public boolean getSignal() {
        return signal;
    }

    @Override
    public void setSignal(boolean signal) {
        this.signal = signal;
    }

    public BinaryGate[] childs() {
        return childs;
    }

    void attachChild(BinaryGate child) {
        if (childs.length >= MAX_CHILDS) {
            throw new UnsupportedOperationException("Gate cannot have more than " + MAX_CHILDS + " children.");
        }
        addChild(child);
    }

    private void addChild(BinaryGate child) {
        assert !Arrays.asList(childs).contains(child);
        BinaryGate[] newChilds = Arrays.copyOf(childs, childs.length + 1);
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
}
