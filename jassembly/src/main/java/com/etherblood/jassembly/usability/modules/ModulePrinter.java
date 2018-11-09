package com.etherblood.jassembly.usability.modules;

import com.etherblood.jassembly.core.BinaryGate;
import com.etherblood.jassembly.core.Wire;
import com.etherblood.jassembly.usability.signals.WireReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Philipp
 */
public class ModulePrinter {

    public String toString(SimpleModule mod) {
        List<String> list = new ArrayList<>();
        List<Wire> wires = new ArrayList<>();
        String in = "in" + mod.inputCount() + "(";
        for (int i = 0; i < mod.inputCount(); i++) {
            WireReference input = mod.getIn(i);
            Wire wire = input.getWire();
            wires.add(wire);
            in += i + ",";
            list.add(i + "(" + wire.getSignal() + ")");
        }
        in = in.substring(0, in.length() - 1) + ")";
        list.add(0, in);
        for (BinaryGate gate : mod.getGates()) {
            wires.add(gate.getOut());
        }
        for (BinaryGate gate : mod.getGates()) {
            Wire out = gate.getOut();
            String s = wires.indexOf(out) + "(" + out.getSignal() + ")= " + wireString(wires, gate.getA()) + " nand " + wireString(wires, gate.getB());
            list.add(s);
        }
        String out = "out" + mod.outputCount() + "(";
        for (Wire output : mod.getOutputs()) {
            out += wireString(wires, output) + ",";
        }
        out = out.substring(0, out.length() - 1) + ")";
        list.add(1, out);
        return list.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    private Object wireString(List<Wire> wires, Wire wire) {
        int index = wires.indexOf(wire);
        if (index >= 0) {
            return index;
        }
        return wire.getSignal();
    }
}
