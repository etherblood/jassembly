package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.core.BinaryGate;
import com.etherblood.jassembly.usability.modules.ReferenceModule;
import com.etherblood.jassembly.usability.modules.wires.InputReference;
import com.etherblood.jassembly.usability.modules.wires.OutputReference;
import com.etherblood.jassembly.usability.modules.wires.RelayWireReference;
import com.etherblood.jassembly.usability.modules.wires.Wires;
import com.etherblood.jassembly.usability.signals.HasSignal;
import com.etherblood.jassembly.usability.signals.SignalRange;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FlipFlopModule extends ReferenceModule {

    private final int width;

    FlipFlopModule(List<InputReference> inputs, List<OutputReference> outputs, SignalRange memory, int width) {
        super(inputs, outputs, memory);
        this.width = width;
    }

    public List<InputReference> getInWord() {
        return getAllInputs().subList(0, width);
    }

    public List<InputReference> getInWrite() {
        return getAllInputs().subList(width, getAllInputs().size());
    }

    public List<OutputReference> getOutWord() {
        return getAllOutputs().subList(0, width);
    }

    public List<OutputReference> getOutWordComplement() {
        return getAllOutputs().subList(width, 2 * width);
    }

    public SignalRange getMemoryWord() {
        return getAllMemory().subRange(0, width);
    }

    public SignalRange getMemoryWordComplement() {
        return getAllMemory().subRange(width, width);
    }

    public static FlipFlopModule register(int width, Consumer<BinaryGate> gates) {
        return msFlipFlop(width, gates);
    }

    public static FlipFlopModule msFlipFlop(int width, Consumer<BinaryGate> gates) {
        FlipFlopModule master = dGatedFlipFlop(width, gates);
        FlipFlopModule slave = dGatedFlipFlop(width, gates);
        UnaryOperationModule not = UnaryOperationModule.not(1, gates);

        Wires.connect(master.getOutWord(), slave.getInWord());
        Wires.connect(not.getOut(), slave.getInWrite());

        RelayWireReference writeSignal = new RelayWireReference();
        Wires.connect(writeSignal, master.getInWrite());
        Wires.connect(writeSignal, not.getIn());

        return new FlipFlopModule(
                Wires.concat(master.getInWord(), Arrays.asList(writeSignal)),
                slave.getAllOutputs(),
                slave.getAllMemory(),
                width);
    }

    public static FlipFlopModule dGatedFlipFlop(int width, Consumer<BinaryGate> gates) {
        FlipFlopModule srFlipFlop = srNandFlipFlop(width, gates);
        BinaryOperationModule set = BinaryOperationModule.nand(width, gates);
        BinaryOperationModule reset = BinaryOperationModule.nand(width, gates);
        Wires.setValue(set.getOut(), ~0, gates);
        Wires.setValue(reset.getOut(), ~0, gates);

        RelayWireReference writeSignal = new RelayWireReference();
        Wires.connect(writeSignal, set.getInB());
        Wires.connect(writeSignal, reset.getInB());

        Wires.connect(set.getOut(), srFlipFlop.getInWord());
        Wires.connect(reset.getOut(), srFlipFlop.getInWrite());
        Wires.connect(set.getOut(), reset.getInA());
        return new FlipFlopModule(
                Wires.concat(set.getInA(), Arrays.asList(writeSignal)),
                srFlipFlop.getAllOutputs(),
                srFlipFlop.getAllMemory(),
                width);
    }

    public static FlipFlopModule srNandFlipFlop(int width, Consumer<BinaryGate> gates) {
        BinaryOperationModule a = BinaryOperationModule.nand(width, gates);
        BinaryOperationModule b = BinaryOperationModule.nand(width, gates);
        Wires.connect(b.getOut(), a.getInB());
        Wires.connect(a.getOut(), b.getInA());
        Wires.setValue(b.getOut(), ~0, gates);
        return new FlipFlopModule(
                Wires.concat(a.getInA(), b.getInB()),
                Wires.concat(a.getOut(), b.getOut()),
                new SignalRange(Stream.concat(a.getOut().stream(), b.getOut().stream())
                        .map(x -> x.getWire())
                        .toArray(HasSignal[]::new)),
                width);
    }

}
