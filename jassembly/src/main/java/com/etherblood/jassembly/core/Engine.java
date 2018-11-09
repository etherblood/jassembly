package com.etherblood.jassembly.core;

import com.etherblood.jassembly.core.collections.FastArrayList;

/**
 *
 * @author Philipp
 */
public class Engine {

    private final FastArrayList<BinaryGate[]> gates = new FastArrayList<>();
    private final FastArrayList<Wire> trues = new FastArrayList<>(), falses = new FastArrayList<>();

    public void tick() {
        for (BinaryGate[] arr : gates) {
            for (BinaryGate gate : arr) {
                boolean currentSignal = gate.getOut().getSignal();
                boolean nextSignal = gate.compute();
                if (nextSignal != currentSignal) {
                    if (nextSignal) {
                        trues.add(gate.getOut());
                    } else {
                        falses.add(gate.getOut());
                    }
                }
            }
        }
        gates.clear();

        for (Wire wire : trues) {
            if (!wire.getSignal()) {
                wire.setSignal(true);
                gates.add(wire.childs());
            }
        }
        trues.clear();

        for (Wire wire : falses) {
            if (wire.getSignal()) {
                wire.setSignal(false);
                gates.add(wire.childs());
            }
        }
        falses.clear();
    }

    public boolean isActive() {
        return gates.size() > 0;
    }

    public void activate(BinaryGate... gates) {
        this.gates.add(gates);
    }

    public void activate(Wire... wires) {
        for (Wire wire : wires) {
            activate(wire.childs());
        }
    }

}
