package com.etherblood.jassembly.usability.modules.wires;

import com.etherblood.jassembly.core.BinaryGate;

public class GateInputBReference extends GateInputReference {

    public GateInputBReference(BinaryGate gate) {
        super(gate);
    }

    @Override
    public void cascade() {
        gate.setB(source.getWire());
    }
}
