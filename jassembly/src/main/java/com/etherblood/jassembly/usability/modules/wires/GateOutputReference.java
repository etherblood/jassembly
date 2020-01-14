package com.etherblood.jassembly.usability.modules.wires;

import com.etherblood.jassembly.core.BinaryGate;
import com.etherblood.jassembly.core.Wire;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GateOutputReference implements OutputReference {

    private final BinaryGate gate;
    private final List<InputReference> consumers = new ArrayList<>();

    public GateOutputReference(BinaryGate gate) {
        this.gate = gate;
    }

    @Override
    public void connectTo(InputReference consumer) {
        consumers.add(Objects.requireNonNull(consumer));
    }

    @Override
    public Wire getWire() {
        return gate.getOut();
    }

    @Override
    public boolean isResolved() {
        return true;
    }

}
