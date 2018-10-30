package com.etherblood.circuit.core;

import com.etherblood.circuit.core.collections.FastArrayList;
import com.etherblood.circuit.core.collections.FastVersionedSet;

/**
 *
 * @author Philipp
 */
public class Engine {

    private final FastVersionedSet<BinaryGate> gates;
    private final FastArrayList<Wire> wires;

    public Engine() {
        this.wires = new FastArrayList<>();
        this.gates = new FastVersionedSet<>(BinaryGate::getId);
    }

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
        return gates.size() > 0;
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
