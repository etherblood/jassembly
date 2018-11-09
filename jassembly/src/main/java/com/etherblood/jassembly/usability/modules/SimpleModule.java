package com.etherblood.jassembly.usability.modules;

import com.etherblood.jassembly.core.BinaryGate;
import com.etherblood.jassembly.core.Wire;
import com.etherblood.jassembly.usability.signals.WireReference;

/**
 *
 * @author Philipp
 */
public class SimpleModule {

    private final WireReference[] inputs;
    private final Wire[] outputs;
    private final BinaryGate[] gates;

    public SimpleModule(WireReference[] inputs, BinaryGate[] gates, Wire[] outputs) {
        this.outputs = outputs;
        this.gates = gates;
        this.inputs = inputs;
    }
    
    public int inputCount() {
        return inputs.length;
    }
    
    public int outputCount() {
        return outputs.length;
    }

    public WireReference getIn(int index) {
        return inputs[index];
    }

    public Wire getOut(int index) {
        return outputs[index];
    }

    public WireReference[] getInputs() {
        return inputs;
    }

    public Wire[] getOutputs() {
        return outputs;
    }

    public BinaryGate[] getGates() {
        return gates;
    }
}
