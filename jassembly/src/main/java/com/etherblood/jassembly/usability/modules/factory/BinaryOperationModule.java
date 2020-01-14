package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.core.BinaryGate;
import com.etherblood.jassembly.core.NandGate;
import com.etherblood.jassembly.usability.modules.ReferenceModule;
import com.etherblood.jassembly.usability.modules.wires.InputReference;
import com.etherblood.jassembly.usability.modules.wires.OutputReference;
import com.etherblood.jassembly.usability.modules.wires.Wires;
import static com.etherblood.jassembly.usability.modules.wires.Wires.inA;
import static com.etherblood.jassembly.usability.modules.wires.Wires.inB;
import static com.etherblood.jassembly.usability.modules.wires.Wires.out;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Philipp
 */
public class BinaryOperationModule extends ReferenceModule {

    BinaryOperationModule(List<InputReference> inputs, List<OutputReference> outputs) {
        super(inputs, outputs);
    }

    public int getWidth() {
        return getAllOutputs().size();
    }

    public List<InputReference> getInA() {
        return getAllInputs().subList(0, getWidth());
    }

    public List<InputReference> getInB() {
        return getAllInputs().subList(getWidth(), 2 * getWidth());
    }

    public List<OutputReference> getOut() {
        return getAllOutputs();
    }

    public static BinaryOperationModule and(int width, Consumer<BinaryGate> gates) {
        BinaryOperationModule nand = nand(width, gates);
        UnaryOperationModule not = UnaryOperationModule.not(width, gates);
        Wires.connect(nand.getOut(), not.getIn());
        return new BinaryOperationModule(nand.getAllInputs(), not.getAllOutputs());
    }

    public static BinaryOperationModule nand(int width, Consumer<BinaryGate> gates) {
        return BinaryOperationModule.create(() -> nand(gates), width);
    }

    private static ReferenceModule nand(Consumer<BinaryGate> gates) {
        NandGate nand = new NandGate();
        gates.accept(nand);
        return new ReferenceModule(
                asList(inA(nand), inB(nand)),
                asList(out(nand)));
    }

    public static BinaryOperationModule create(Supplier<ReferenceModule> supply, int width) {
        List<ReferenceModule> modules = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            modules.add(supply.get());
        }
        List<InputReference> inputs = new ArrayList<>();
        List<OutputReference> outputs = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            ReferenceModule module = modules.get(i);
            inputs.add(module.getAllInputs().get(0));
        }
        for (int i = 0; i < width; i++) {
            ReferenceModule module = modules.get(i);
            inputs.add(module.getAllInputs().get(1));
        }
        for (int i = 0; i < width; i++) {
            ReferenceModule module = modules.get(i);
            outputs.add(module.getAllOutputs().get(0));
        }
        return new BinaryOperationModule(inputs, outputs);
    }

}
