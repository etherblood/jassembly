package com.etherblood.jassembly.usability.modules.wires;

import com.etherblood.jassembly.core.BinaryGate;

public class GateInputAReference extends GateInputReference {

    public GateInputAReference(BinaryGate gate) {
        super(gate);
    }

    @Override
    public void cascade() {
        gate.setA(source.getWire());
    }
}
