package com.etherblood.jassembly.usability.modules;

import com.etherblood.jassembly.core.BinaryGate;
import com.etherblood.jassembly.core.Wire;
import com.etherblood.jassembly.usability.signals.SignalRange;
import com.etherblood.jassembly.usability.signals.WireReference;


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
