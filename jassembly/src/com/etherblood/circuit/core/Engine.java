package com.etherblood.circuit.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Philipp
 */
public class Engine {

    private final Set<BinaryGate> gates = new HashSet<>();
    private final List<Wire> wires = new ArrayList<>();

    public void tick() {
        for (BinaryGate gate : gates) {//can be parallel (make collections concurrent)
            gate.compute();
            Wire out = gate.getOut();
            if (out.requiresUpdate()) {
                wires.add(out);
            }
        }
        gates.clear();
        for (Wire wire : wires) {//can be parallel (make collections concurrent)
            wire.update();
            activate(wire.childs());
        }
        wires.clear();
    }

    public boolean isActive() {
        return !gates.isEmpty();
    }

    public void activate(BinaryGate... gates) {
        for (BinaryGate gate : gates) {
            this.gates.add(gate);
        }
    }

    public void activate(Iterable<BinaryGate> gates) {
        for (BinaryGate gate : gates) {
            this.gates.add(gate);
        }
    }

    public void activate(Wire... wires) {
        for (Wire wire : wires) {
            activate(wire.childs());
        }
    }

}
