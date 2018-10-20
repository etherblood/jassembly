package com.etherblood.circuit.usability.signals;

/**
 *
 * @author Philipp
 */
public class CombinedSignal implements HasSignal {

    private final HasSignal[] signals;

    public CombinedSignal(HasSignal... signals) {
        this.signals = signals;
    }

    @Override
    public boolean getSignal() {
        for (HasSignal signal : signals) {
            return signal.getSignal();
        }
        throw new IllegalStateException();
    }

    @Override
    public void setSignal(boolean value) {
        for (HasSignal signal : signals) {
            signal.setSignal(value);
        }
    }

}
