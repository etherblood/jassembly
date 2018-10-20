package com.etherblood.circuit.usability.modules;

import com.etherblood.circuit.core.BinaryGate;
import com.etherblood.circuit.core.Wire;
import com.etherblood.circuit.usability.signals.SignalRange;
import com.etherblood.circuit.usability.signals.WireReference;


public class MemoryModule extends SimpleModule {
    
    private final SignalRange signal;

    public MemoryModule(SignalRange signal, WireReference[] inputs, BinaryGate[] gates, Wire[] outputs) {
        super(inputs, gates, outputs);
        this.signal = signal;
    }
    
    public SignalRange getSignals() {
        return signal;
    }
}
