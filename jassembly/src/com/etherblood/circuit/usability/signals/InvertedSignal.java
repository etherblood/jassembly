package com.etherblood.circuit.usability.signals;

/**
 *
 * @author Philipp
 */
public class InvertedSignal implements HasSignal {

    private final HasSignal source;

    public InvertedSignal(HasSignal source) {
        this.source = source;
    }

    @Override
    public boolean getSignal() {
        return !source.getSignal();
    }

    @Override
    public void setSignal(boolean value) {
        source.setSignal(!value);
    }
}
