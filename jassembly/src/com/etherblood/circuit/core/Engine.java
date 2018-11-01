package com.etherblood.circuit.core;

import com.etherblood.circuit.core.collections.FastArrayList;

/**
 *
 * @author Philipp
 */
public class Engine {

    private final FastArrayList<BinaryGate[]> gates = new FastArrayList<>();
    private final FastArrayList<Wire> trues = new FastArrayList<>(), falses = new FastArrayList<>();

    public void tick() {
        for (BinaryGate[] arr : gates) { //can be parallel (make collections concurrent)
            for (BinaryGate gate : arr) {
                boolean value = gate.compute();
                if (value == gate.getOut().getSignal()) {
//                    early exit
                    continue;
                }
                if (value) {
                    trues.add(gate.getOut());
                } else {
                    falses.add(gate.getOut());
                }
            }
        }
        gates.clear();

        for (Wire wire : trues) {//can be parallel (make collections concurrent)
            if (!wire.getSignal()) {
                wire.setSignal(true);
                gates.add(wire.childs());
            }
        }
        trues.clear();

        for (Wire wire : falses) {//can be parallel (make collections concurrent)
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
