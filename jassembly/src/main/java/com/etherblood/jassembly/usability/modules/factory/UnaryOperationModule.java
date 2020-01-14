package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.core.BinaryGate;
import com.etherblood.jassembly.core.NandGate;
import com.etherblood.jassembly.core.Wire;
import com.etherblood.jassembly.usability.modules.ReferenceModule;
import com.etherblood.jassembly.usability.modules.wires.InputReference;
import com.etherblood.jassembly.usability.modules.wires.OutputReference;
import com.etherblood.jassembly.usability.modules.wires.RelayWireReference;
import static com.etherblood.jassembly.usability.modules.wires.Wires.inA;
import static com.etherblood.jassembly.usability.modules.wires.Wires.out;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Philipp
 */
public class UnaryOperationModule extends ReferenceModule {

    UnaryOperationModule(List<InputReference> inputs, List<OutputReference> outputs) {
        super(inputs, outputs);
    }

    public List<InputReference> getIn() {
        return getAllInputs();
    }

    public List<OutputReference> getOut() {
        return getAllOutputs();
    }

    public int getWidth() {
        return getAllOutputs().size();
    }

    public static UnaryOperationModule identity(int width, Consumer<BinaryGate> gates) {
        RelayWireReference[] wires = new RelayWireReference[width];
        for (int i = 0; i < width; i++) {
            wires[i] = new RelayWireReference();
        }
        return new UnaryOperationModule(asList(wires), asList(wires));
    }

    public static UnaryOperationModule reverse(int width, Consumer<BinaryGate> gates) {
        RelayWireReference[] wires = new RelayWireReference[width];
        for (int i = 0; i < width; i++) {
            wires[i] = new RelayWireReference();
        }
        List<InputReference> inputs = asList(wires);
        Collections.reverse(inputs);
        return new UnaryOperationModule(inputs, asList(wires));
    }

    public static UnaryOperationModule not(int width, Consumer<BinaryGate> gates) {
        return UnaryOperationModule.create(() -> not(gates), width);
    }

    private static ReferenceModule not(Consumer<BinaryGate> gates) {
        NandGate nand = new NandGate();
        nand.setB(Wire.on());
        gates.accept(nand);
        return new ReferenceModule(
                asList(inA(nand)),
                asList(out(nand)));
    }

    public static UnaryOperationModule create(Supplier<ReferenceModule> supply, int width) {
        List<ReferenceModule> modules = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            modules.add(supply.get());
        }
        List<InputReference> inputs = new ArrayList<>();
        List<OutputReference> outputs = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            ReferenceModule module = modules.get(i);
            inputs.add(module.getAllInputs().get(0));
            outputs.add(module.getAllOutputs().get(0));
        }
        return new UnaryOperationModule(inputs, outputs);
    }

}
