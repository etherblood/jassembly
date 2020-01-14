package com.etherblood.jassembly.usability.modules.wires;

import com.etherblood.jassembly.core.BinaryGate;
import java.util.Objects;

public abstract class GateInputReference implements InputReference {

    protected final BinaryGate gate;
    protected OutputReference source;

    public GateInputReference(BinaryGate gate) {
        this.gate = gate;
    }

    @Override
    public void connectTo(OutputReference source) {
        if (this.source != null) {
            throw new IllegalStateException();
        }
        this.source = Objects.requireNonNull(source);
        if(this.source.isResolved()) {
            cascade();
        }
    }

}
