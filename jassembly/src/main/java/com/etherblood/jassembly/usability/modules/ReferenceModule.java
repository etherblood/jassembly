package com.etherblood.jassembly.usability.modules;

import com.etherblood.jassembly.usability.modules.wires.InputReference;
import com.etherblood.jassembly.usability.modules.wires.OutputReference;
import com.etherblood.jassembly.usability.signals.SignalRange;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class ReferenceModule {

    private final List<InputReference> inputs;
    private final List<OutputReference> outputs;
    private final SignalRange memory;

    public ReferenceModule(List<InputReference> inputs, List<OutputReference> outputs) {
        this(inputs, outputs, new SignalRange());
    }

    public ReferenceModule(List<InputReference> inputs, List<OutputReference> outputs, SignalRange memory) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.memory = memory;
    }

    public List<InputReference> getAllInputs() {
        return inputs;
    }

    public List<OutputReference> getAllOutputs() {
        return outputs;
    }

    public SignalRange getAllMemory() {
        return memory;
    }
}
