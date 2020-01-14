package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.core.Engine;
import com.etherblood.jassembly.core.Wire;
import com.etherblood.jassembly.usability.modules.wires.InputReference;
import com.etherblood.jassembly.usability.modules.wires.OutputReference;
import com.etherblood.jassembly.usability.modules.wires.WireOutputReference;
import com.etherblood.jassembly.usability.modules.wires.Wires;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public abstract class ReferenceModuleTestBase {

    protected final Engine engine = new Engine();
    
    protected List<WireOutputReference> variable(List<? extends InputReference> input) {
        List<WireOutputReference> result = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            result.add(new WireOutputReference(Wire.mutable(false)));
        }
        Wires.connect(result, input);
        return result;
    }

    protected void setValue(List<? extends WireOutputReference> word, long value) {
        Wires.setValue(word, value, engine::activate);
    }

    protected long getValue(List<? extends OutputReference> word) {
        return Wires.getValue(word);
    }

    protected void compute() {
        while (engine.isActive()) {
            engine.tick();
        }
    }
}
