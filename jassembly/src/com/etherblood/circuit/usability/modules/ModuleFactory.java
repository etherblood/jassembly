package com.etherblood.circuit.usability.modules;

import static com.etherblood.circuit.usability.modules.ModuleUtil.*;
import com.etherblood.circuit.core.BinaryGate;
import com.etherblood.circuit.core.Engine;
import com.etherblood.circuit.core.NandGate;
import com.etherblood.circuit.core.Wire;
import com.etherblood.circuit.usability.signals.HasSignal;
import com.etherblood.circuit.usability.signals.SignalRange;
import com.etherblood.circuit.usability.signals.WireReference;
import com.etherblood.circuit.usability.signals.InvertedSignal;
import com.etherblood.circuit.usability.signals.CombinedSignal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author Philipp
 */
public class ModuleFactory {

    public SimpleModule logicUnit(int width) {
        int depth = 2;
        WireReference[] inA = new WireReference[width];
        WireReference[] inB = new WireReference[width];
        WireReference[] inSelect = new WireReference[depth];
        Wire[] out = new Wire[width];
        List<BinaryGate> gates = new ArrayList<>();
        
        SimpleModule demuxA = demultiplexer(width, depth);
        SimpleModule demuxB = demultiplexer(width, depth);
        SimpleModule mux = multiplexer(width, depth);
        SimpleModule and = parallelBinary(this::and, width);
        SimpleModule or = parallelBinary(this::or, width);
        SimpleModule xor = parallelBinary(this::xor, width);
        
        for (int i = 0; i < width; i++) {
            inA[i] = demuxA.getIn(i);
            inB[i] = demuxB.getIn(i);
            out[i] = mux.getOut(i);
            
            and.getIn(i).setWire(demuxA.getOut(i));
            and.getIn(width + i).setWire(demuxB.getOut(i));
            mux.getIn(i).setWire(and.getOut(i));
            
            or.getIn(i).setWire(demuxA.getOut(width + i));
            or.getIn(width + i).setWire(demuxB.getOut(width + i));
            mux.getIn(width + i).setWire(or.getOut(i));
            
            xor.getIn(i).setWire(demuxA.getOut(2 * width + i));
            xor.getIn(width + i).setWire(demuxB.getOut(2 * width + i));
            mux.getIn(2 * width + i).setWire(xor.getOut(i));
        }
        for (int i = 0; i < depth; i++) {
            inSelect[i] = combine(demuxA.getIn(width + i), demuxB.getIn(width + i), mux.getIn(4 * width + i));
        }
        gates.addAll(Arrays.asList(demuxA.getGates()));
        gates.addAll(Arrays.asList(demuxB.getGates()));
        gates.addAll(Arrays.asList(mux.getGates()));
        gates.addAll(Arrays.asList(and.getGates()));
        gates.addAll(Arrays.asList(or.getGates()));
        gates.addAll(Arrays.asList(xor.getGates()));
        
        return new SimpleModule(concat(inA, inB, inSelect), gates.toArray(new BinaryGate[gates.size()]), out);
    }
    
    public SimpleModule logicalRightShift(int width, int depth) {
        SimpleModule[] layers = new SimpleModule[depth];
        WireReference[] select = new WireReference[depth];
        List<BinaryGate> gates = new ArrayList<>();
        for (int layer = 0; layer < depth; layer++) {
            layers[layer] = rightShiftLayer(width, 1 << layer);
            select[layer] = layers[layer].getIn(width);
            gates.addAll(Arrays.asList(layers[layer].getGates()));
        }
        for (int layer = 0; layer < depth - 1; layer++) {
            for (int bit = 0; bit < width; bit++) {
                layers[layer + 1].getIn(bit).setWire(layers[layer].getOut(bit));
            }
        }
        return new SimpleModule(
                concat(Arrays.copyOf(layers[0].getInputs(), width), select),
                gates.toArray(new BinaryGate[gates.size()]),
                layers[depth - 1].getOutputs());
    }

    private SimpleModule rightShiftLayer(int width, int shift) {
        WireReference[] in = new WireReference[width];
        WireReference[] select = new WireReference[width];
        Wire[] out = new Wire[width];
        SimpleModule[] muxes = new SimpleModule[width];
        List<BinaryGate> gates = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            muxes[i] = multiplexer();
            out[i] = muxes[i].getOut(0);
            in[i] = muxes[i].getIn(0);
            select[i] = muxes[i].getIn(2);
            gates.addAll(Arrays.asList(muxes[i].getGates()));
        }
        for (int i = 0; i < width; i++) {
            WireReference in1 = muxes[i].getIn(1);
            int shifted = i + shift;
            if (shifted < width) {
                in[shifted] = combine(in[shifted], in1);
            } else {
                in1.setWire(Wire.off());
            }
        }
        return new SimpleModule(concat(in, array(combine(select))),
                gates.toArray(new BinaryGate[gates.size()]),
                out);
    }

    public SimpleModule signalModifier(int width) {
        int depth = 2;
        WireReference[] inputs = new WireReference[width];
        List<BinaryGate> gates = new ArrayList<>();
        SimpleModule mux = multiplexer(width, depth);
        SimpleModule demux = demultiplexer(width, depth);
        SimpleModule any = any(width);
        SimpleModule not = not(width);
        gates.addAll(Arrays.asList(mux.getGates()));
        gates.addAll(Arrays.asList(demux.getGates()));
        gates.addAll(Arrays.asList(any.getGates()));
        gates.addAll(Arrays.asList(not.getGates()));

        for (int i = 0; i < width; i++) {
            mux.getIn(i).setWire(demux.getOut(i));

            mux.getIn(width + i).setWire(any.getOut(0));
            any.getIn(i).setWire(demux.getOut(width + i));

            mux.getIn(2 * width + i).setWire(not.getOut(i));
            not.getIn(i).setWire(demux.getOut(2 * width + i));

            mux.getIn(3 * width + i).setWire(demux.getOut(4 * width - i - 1));
            inputs[i] = demux.getIn(i);
        }
        return new SimpleModule(
                concat(inputs, array(combine(mux.getIn(width * (1 << depth)), demux.getIn(width)), combine(mux.getIn(width * (1 << depth) + 1), demux.getIn(width + 1)))),
                gates.toArray(new BinaryGate[gates.size()]),
                mux.getOutputs());
    }

    public SimpleModule any(int width) {
        if (width <= 2) {
            return or();
        }
        SimpleModule a = any(width / 2);
        SimpleModule b = any(width / 2);
        SimpleModule or = or();
        or.getIn(0).setWire(a.getOut(0));
        or.getIn(1).setWire(b.getOut(0));
        Wire[] out = new Wire[width];
        Arrays.fill(out, or.getOut(0));
        return new SimpleModule(
                concat(a.getInputs(), b.getInputs()),
                concat(a.getGates(), b.getGates(), or.getGates()),
                out);
    }

    public SimpleModule noop(int inCount, int outCount) {
        WireReference[] in = new WireReference[inCount];
        for (int i = 0; i < inCount; i++) {
            in[i] = new WireReference() {
                private Wire wire = Wire.off();

                @Override
                public Wire getWire() {
                    return wire;
                }

                @Override
                public void setWire(Wire wire) {
                    this.wire = wire;
                }
            };
        }
        Wire[] out = new Wire[outCount];
        for (int i = 0; i < outCount; i++) {
            out[i] = Wire.off();
        }
        return new SimpleModule(in, new BinaryGate[0], out);
    }

    public SimpleModule incrementer(int width) {
        List<BinaryGate> gates = new ArrayList<>();
        SimpleModule[] halfAdders = new SimpleModule[width];
        WireReference[] in = new WireReference[width];
        Wire[] out = new Wire[width];
        for (int i = 0; i < width; i++) {
            SimpleModule halfAdder = halfAdder();
            halfAdders[i] = halfAdder;
            gates.addAll(Arrays.asList(halfAdder.getGates()));
            in[i] = halfAdder.getIn(0);
            out[i] = halfAdder.getOut(0);
            halfAdder.getIn(0).setWire(halfAdder.getOut(1));
        }
        for (int i = 1; i < width; i++) {
            SimpleModule a = halfAdders[i - 1];
            SimpleModule b = halfAdders[i];
            b.getIn(1).setWire(a.getOut(1));
        }
        return new SimpleModule(
                concat(in, array(halfAdders[0].getIn(1))),
                gates.toArray(new BinaryGate[gates.size()]),
                out);
    }

    public SimpleModule counter(int width) {
        List<BinaryGate> gates = new ArrayList<>();
        SimpleModule[] flipFlops = new SimpleModule[width];
        Wire[] out = new Wire[width];
        for (int i = 0; i < width; i++) {
            SimpleModule flipFlop = msFlipFlop();
            flipFlops[i] = flipFlop;
            gates.addAll(Arrays.asList(flipFlop.getGates()));
            out[i] = flipFlop.getOut(0);
            flipFlop.getIn(0).setWire(flipFlop.getOut(1));
        }
        for (int i = 1; i < width; i++) {
            SimpleModule a = flipFlops[i - 1];
            SimpleModule b = flipFlops[i];
            b.getIn(1).setWire(a.getOut(0));
        }
        return new SimpleModule(
                array(flipFlops[0].getIn(1)),
                gates.toArray(new BinaryGate[gates.size()]),
                out);
    }

    public MemoryModule msFlipFlop(int width) {
        WireReference[] inputs = new WireReference[width];
        WireReference[] inputsSelect = new WireReference[width];
        List<BinaryGate> gates = new ArrayList<>();
        Wire[] outputs = new Wire[width];
        Wire[] outputsInverted = new Wire[width];
        HasSignal[] signals = new HasSignal[width];
        for (int i = 0; i < width; i++) {
            MemoryModule flipFlop = msFlipFlop();
            signals[i] = flipFlop.getSignals().get(0);
            inputs[i] = flipFlop.getIn(0);
            inputsSelect[i] = flipFlop.getIn(1);
            outputs[i] = flipFlop.getOut(0);
            outputsInverted[i] = flipFlop.getOut(1);
            gates.addAll(Arrays.asList(flipFlop.getGates()));
        }
        return new MemoryModule(
                new SignalRange(signals),
                concat(inputs, array(combine(inputsSelect))),
                gates.toArray(new BinaryGate[gates.size()]),
                concat(outputs, outputsInverted));
    }

    public MemoryModule msFlipFlop() {
        MemoryModule master = dGatedFlipFlop();
        MemoryModule slave = dGatedFlipFlop();
        SimpleModule not = not();

        slave.getIn(0).setWire(master.getOut(0));
        slave.getIn(1).setWire(not.getOut(0));

        return new MemoryModule(
                new SignalRange(new CombinedSignal(master.getSignals().get(0), slave.getSignals().get(0))),
                array(master.getIn(0), combine(master.getIn(1), not.getIn(0))),
                concat(master.getGates(), slave.getGates(), not.getGates()),
                slave.getOutputs());
    }
    
    public MemoryModule ram(int width) {
        return ram(width, Integer.MAX_VALUE);
    }

    public MemoryModule ram(int width, int maxWords) {
        int addressSpace = 1 << width;
        int words = Math.min(maxWords, addressSpace);
        SimpleModule demux = demultiplexer(width + 1, width);
        SimpleModule mux = multiplexer(width, width);

        //TODO: fix
        Engine engine = new Engine();
        engine.activate(demux.getGates());
        while (engine.isActive()) {
            engine.tick();
        }

        WireReference[] address = new WireReference[width];
        List<BinaryGate> gates = new ArrayList<>();
        HasSignal[] signals = new HasSignal[words * width];
        for (int word = 0; word < words; word++) {
            MemoryModule dFlipFlop = dGatedFlipFlop(width);
            for (int bit = 0; bit < width; bit++) {
                signals[word * width + bit] = dFlipFlop.getSignals().get(bit);
                dFlipFlop.getIn(bit).setWire(demux.getOut(word * (width + 1) + bit));
                mux.getIn(word * width + bit).setWire(dFlipFlop.getOut(bit));
            }
            dFlipFlop.getIn(width).setWire(demux.getOut(word * (width + 1) + width));
            gates.addAll(Arrays.asList(dFlipFlop.getGates()));
        }
        for (int bit = 0; bit < width; bit++) {
            address[bit] = combine(mux.getIn(addressSpace * width + bit), demux.getIn(width + 1 + bit));
        }

        return new MemoryModule(
                new SignalRange(signals),
                concat(Arrays.copyOf(demux.getInputs(), width), address, array(demux.getIn(width))),
                concat(demux.getGates(), mux.getGates(), gates.toArray(new BinaryGate[gates.size()])),
                mux.getOutputs());
    }

    public SimpleModule multiplexer(int width, int depth) {
        if (depth == 1) {
            return multiplexer(width);
        }
        WireReference[] signal = new WireReference[depth];
        SimpleModule mux = multiplexer(width);
        SimpleModule demux = demultiplexer(depth - 1);
        SimpleModule muxA = multiplexer(width, depth - 1);
        SimpleModule muxB = multiplexer(width, depth - 1);
        int signalIndex = (1 << (depth - 1)) * width;
        WireReference[] inputsA = Arrays.copyOf(muxA.getInputs(), signalIndex);
        WireReference[] inputsB = Arrays.copyOf(muxB.getInputs(), signalIndex);
        for (int i = 0; i < depth - 1; i++) {
            signal[i] = demux.getIn(i);
            muxA.getIn(signalIndex + i).setWire(demux.getOut(i));
            muxB.getIn(signalIndex + i).setWire(demux.getOut(i + depth - 1));
        }
        signal[depth - 1] = combine(mux.getIn(2 * width), demux.getIn(depth - 1));
        for (int i = 0; i < width; i++) {
            mux.getIn(i).setWire(muxA.getOut(i));
            mux.getIn(i + width).setWire(muxB.getOut(i));
        }
        return new SimpleModule(
                concat(inputsA, inputsB, signal),
                concat(mux.getGates(), muxA.getGates(), muxB.getGates(), demux.getGates()),
                mux.getOutputs());
    }

    public SimpleModule multiplexer(int width) {
        WireReference[] inputsA = new WireReference[width];
        WireReference[] inputsB = new WireReference[width];
        WireReference[] inputsSelect = new WireReference[width];
        List<BinaryGate> gates = new ArrayList<>();
        Wire[] outputs = new Wire[width];
        for (int i = 0; i < width; i++) {
            SimpleModule multiplexer = multiplexer();
            inputsA[i] = multiplexer.getIn(0);
            inputsB[i] = multiplexer.getIn(1);
            inputsSelect[i] = multiplexer.getIn(2);
            outputs[i] = multiplexer.getOut(0);
            gates.addAll(Arrays.asList(multiplexer.getGates()));
        }
        return new SimpleModule(
                concat(inputsA, inputsB, array(combine(inputsSelect))),
                gates.toArray(new BinaryGate[gates.size()]),
                outputs);
    }

    public SimpleModule multiplexer() {
        SimpleModule a = and();
        SimpleModule b = and();
        SimpleModule or = or();
        SimpleModule not = not();
        a.getIn(1).setWire(not.getOut(0));
        or.getIn(0).setWire(a.getOut(0));
        or.getIn(1).setWire(b.getOut(0));
        return new SimpleModule(
                array(a.getIn(0), b.getIn(0), combine(not.getIn(0), b.getIn(1))),
                concat(a.getGates(), b.getGates(), or.getGates(), not.getGates()),
                array(or.getOut(0)));
    }

    public SimpleModule demultiplexer(int width, int depth) {
        assert depth >= 1;
        if (depth == 1) {
            return demultiplexer(width);
        }
        int subSize = width + depth - 1;
        SimpleModule demux = demultiplexer(subSize);
        SimpleModule demuxA = demultiplexer(width, depth - 1);
        SimpleModule demuxB = demultiplexer(width, depth - 1);
        for (int i = 0; i < subSize; i++) {
            demuxA.getIn(i).setWire(demux.getOut(i));
            demuxB.getIn(i).setWire(demux.getOut(subSize + i));
        }
        WireReference[] inputs = Arrays.copyOf(demux.getInputs(), subSize);
        return new SimpleModule(
                concat(inputs, array(demux.getIn(subSize))),
                concat(demux.getGates(), demuxA.getGates(), demuxB.getGates()),
                concat(demuxA.getOutputs(), demuxB.getOutputs()));
    }

    public SimpleModule demultiplexer(int width) {
        WireReference[] inputs = new WireReference[width];
        WireReference[] inputsSelect = new WireReference[width];
        List<BinaryGate> gates = new ArrayList<>();
        Wire[] outputsA = new Wire[width];
        Wire[] outputsB = new Wire[width];
        for (int i = 0; i < width; i++) {
            SimpleModule demultiplexer = demultiplexer();
            inputs[i] = demultiplexer.getIn(0);
            inputsSelect[i] = demultiplexer.getIn(1);
            outputsA[i] = demultiplexer.getOut(0);
            outputsB[i] = demultiplexer.getOut(1);
            gates.addAll(Arrays.asList(demultiplexer.getGates()));
        }
        return new SimpleModule(
                concat(inputs, array(combine(inputsSelect))),
                gates.toArray(new BinaryGate[gates.size()]),
                concat(outputsA, outputsB));
    }

    public SimpleModule demultiplexer() {
        SimpleModule a = and();
        SimpleModule b = and();
        SimpleModule not = not();
        a.getIn(1).setWire(not.getOut(0));
        return new SimpleModule(
                array(combine(a.getIn(0), b.getIn(0)), combine(not.getIn(0), b.getIn(1))),
                concat(a.getGates(), b.getGates(), not.getGates()),
                array(a.getOut(0), b.getOut(0)));
    }

    public MemoryModule dGatedFlipFlop(int width) {
        WireReference[] inputs = new WireReference[width];
        WireReference[] inputsSelect = new WireReference[width];
        List<BinaryGate> gates = new ArrayList<>();
        Wire[] outputs = new Wire[width];
        Wire[] outputsInverted = new Wire[width];
        HasSignal[] signals = new HasSignal[width];
        for (int i = 0; i < width; i++) {
            MemoryModule dFlipFlop = dGatedFlipFlop();
            signals[i] = dFlipFlop.getSignals().get(0);
            inputs[i] = dFlipFlop.getIn(0);
            inputsSelect[i] = dFlipFlop.getIn(1);
            outputs[i] = dFlipFlop.getOut(0);
            outputsInverted[i] = dFlipFlop.getOut(1);
            gates.addAll(Arrays.asList(dFlipFlop.getGates()));
        }
        return new MemoryModule(
                new SignalRange(signals),
                concat(inputs, array(combine(inputsSelect))),
                gates.toArray(new BinaryGate[gates.size()]),
                concat(outputs, outputsInverted));
    }

    public MemoryModule dGatedFlipFlop() {
        MemoryModule srFlipFlop = srNandFlipFlop();
        SimpleModule a = nand();
        SimpleModule b = nand();
        a.getOut(0).setSignal(true);
        b.getOut(0).setSignal(true);
        srFlipFlop.getIn(0).setWire(a.getOut(0));
        srFlipFlop.getIn(1).setWire(b.getOut(0));
        b.getIn(0).setWire(a.getOut(0));
        return new MemoryModule(
                srFlipFlop.getSignals(),
                array(a.getIn(0), combine(a.getIn(1), b.getIn(1))),
                concat(srFlipFlop.getGates(), a.getGates(), b.getGates()),
                array(srFlipFlop.getOut(0), srFlipFlop.getOut(1)));
    }

    public MemoryModule srNandFlipFlop() {
        NandGate a = new NandGate();
        NandGate b = new NandGate();
        a.setB(b.getOut());
        b.setA(a.getOut());
        b.getOut().setSignal(true);
        return new MemoryModule(
                new SignalRange(new CombinedSignal(a.getOut(), new InvertedSignal(b.getOut()))),
                array(inA(a), inB(b)),
                array(a, b),
                array(a.getOut(), b.getOut()));
    }

    public SimpleModule rippleCarryAdder(int width) {
        WireReference[] inputsA = new WireReference[width];
        WireReference[] inputsB = new WireReference[width];
        List<BinaryGate> gates = new ArrayList<>();
        Wire[] outputs = new Wire[width + 1];

        WireReference inputC = null;
        Wire carry = Wire.off();
        for (int i = 0; i < width; i++) {
            SimpleModule adder = fullAdder();
            if (inputC == null) {
                inputC = adder.getIn(2);
            }
            adder.getIn(2).setWire(carry);
            inputsA[i] = adder.getIn(0);
            inputsB[i] = adder.getIn(1);
            outputs[i] = adder.getOut(0);
            carry = adder.getOut(1);
            gates.addAll(Arrays.asList(adder.getGates()));
        }
        outputs[width] = carry;

        return new SimpleModule(
                concat(inputsA, inputsB, array(inputC)),
                gates.toArray(new BinaryGate[gates.size()]),
                outputs);
    }

    public SimpleModule fullAdder() {
        SimpleModule a = halfAdder();
        SimpleModule b = halfAdder();
        b.getIn(0).setWire(a.getOut(0));
        SimpleModule or = or();
        or.getIn(0).setWire(a.getOut(1));
        or.getIn(1).setWire(b.getOut(1));
        return new SimpleModule(
                array(a.getIn(0), a.getIn(1), b.getIn(1)),
                array(concat(a.getGates(), b.getGates(), or.getGates())),
                array(b.getOut(0), or.getOut(0))
        );
    }

    public SimpleModule halfAdder() {
        SimpleModule and = and();
        SimpleModule xor = xor();
        return new SimpleModule(
                array(combine(and.getIn(0), xor.getIn(0)), combine(and.getIn(1), xor.getIn(1))),
                array(concat(and.getGates(), xor.getGates())),
                array(xor.getOut(0), and.getOut(0))
        );
    }

    public SimpleModule not(int width) {
        WireReference[] inputs = new WireReference[width];
        List<BinaryGate> gates = new ArrayList<>();
        Wire[] outputs = new Wire[width];

        for (int i = 0; i < width; i++) {
            SimpleModule module = not();
            inputs[i] = module.getIn(0);
            outputs[i] = module.getOut(0);
            gates.addAll(Arrays.asList(module.getGates()));
        }
        return new SimpleModule(
                inputs,
                gates.toArray(new BinaryGate[gates.size()]),
                outputs);
    }

    public SimpleModule not() {
        NandGate a = new NandGate();
        a.setB(Wire.on());
        return new SimpleModule(
                array(inA(a)),
                array(a),
                array(a.getOut()));
    }

    public SimpleModule parallelBinary(Supplier<SimpleModule> supply, int width) {
        WireReference[] inputsA = new WireReference[width];
        WireReference[] inputsB = new WireReference[width];
        List<BinaryGate> gates = new ArrayList<>();
        Wire[] outputs = new Wire[width];
        for (int i = 0; i < width; i++) {
            SimpleModule module = supply.get();
            inputsA[i] = module.getIn(0);
            inputsB[i] = module.getIn(1);
            outputs[i] = module.getOut(0);
            gates.addAll(Arrays.asList(module.getGates()));
        }
        return new SimpleModule(
                concat(inputsA, inputsB),
                gates.toArray(new BinaryGate[gates.size()]),
                outputs);
    }

    public SimpleModule nand() {
        NandGate a = new NandGate();
        return new SimpleModule(
                array(inA(a), inB(a)),
                array(a),
                array(a.getOut()));
    }

    public SimpleModule and() {
        NandGate a = new NandGate();
        NandGate b = new NandGate();

        b.setA(a.getOut());
        b.setB(a.getOut());

        return new SimpleModule(
                array(inA(a), inB(a)),
                array(a, b),
                array(b.getOut()));
    }

    public SimpleModule or() {
        NandGate a = new NandGate();
        NandGate b = new NandGate();
        NandGate c = new NandGate();

        c.setA(a.getOut());
        c.setB(b.getOut());

        return new SimpleModule(
                array(combine(inA(a), inB(a)), combine(inA(b), inB(b))),
                array(a, b, c),
                array(c.getOut()));
    }

    public SimpleModule xor() {
        NandGate a = new NandGate();
        NandGate b = new NandGate();
        NandGate c = new NandGate();
        NandGate d = new NandGate();

        b.setB(a.getOut());
        c.setA(a.getOut());
        d.setA(b.getOut());
        d.setB(c.getOut());

        return new SimpleModule(
                array(combine(inA(a), inA(b)), combine(inB(a), inB(c))),
                array(a, b, c, d),
                array(d.getOut()));
    }

    public SimpleModule pulse() {
        NandGate nand = new NandGate();
        Wire out = nand.getOut();
        nand.setA(out);
        nand.setB(out);
        return new SimpleModule(array(), array(nand), array(nand.getOut()));
    }

}
