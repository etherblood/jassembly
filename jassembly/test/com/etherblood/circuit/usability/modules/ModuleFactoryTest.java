package com.etherblood.circuit.usability.modules;

import com.etherblood.circuit.usability.signals.WireReference;
import com.etherblood.circuit.core.Engine;
import com.etherblood.circuit.core.Wire;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Philipp
 */
public class ModuleFactoryTest {

    private final ModuleFactory factory = new ModuleFactory();
    private final ModulePrinter printer = new ModulePrinter();

    @Test
    public void logicUnit() {
        int width = 7;
        SimpleModule lu = factory.logicUnit(width);
        assertEquals(2 * width + 2, lu.inputCount());
        assertEquals(width, lu.outputCount());
        int a = 5;
        int b = 3;
        setInput(lu, 0, width, a);
        setInput(lu, width, width, b);
        
        setInput(lu, 2 * width, 2, 0);
        compute(lu);
        assertEquals(a & b, getOutput(lu, 0, width));
        
        setInput(lu, 2 * width, 2, 1);
        compute(lu);
        assertEquals(a | b, getOutput(lu, 0, width));
        
        setInput(lu, 2 * width, 2, 2);
        compute(lu);
        assertEquals(a ^ b, getOutput(lu, 0, width));
        
    }
    
    @Test
    public void logicalRightShift() {
        int depth = 3;
        int width = 1 << depth;
        int value = 157;
        int shift = 3;
        SimpleModule shifter = factory.logicalRightShift(width, depth);
        assertEquals(width + depth, shifter.inputCount());
        assertEquals(width, shifter.outputCount());
        setInput(shifter, 0, width, value);
        setInput(shifter, width, depth, shift);
        compute(shifter);
        assertEquals(value >> shift, getOutput(shifter, 0, width));
    }

    @Test
    public void signalModifier() {
        int width = 4;
        SimpleModule mod = factory.signalModifier(width);
        assertEquals(width + 2, mod.inputCount());
        assertEquals(width, mod.outputCount());
        setInput(mod, 0, width, 7);

        setInput(mod, width, 2, 0);
        compute(mod);
        assertEquals(7, getOutput(mod, 0, width));

        setInput(mod, width, 2, 1);
        compute(mod);
        assertEquals(15, getOutput(mod, 0, width));

        setInput(mod, width, 2, 2);
        compute(mod);
        assertEquals(8, getOutput(mod, 0, width));

        setInput(mod, width, 2, 3);
        compute(mod);
        assertEquals(14, getOutput(mod, 0, width));
    }

    @Test
    public void counter() {
        int width = 32;
        SimpleModule counter = factory.counter(width);
        assertEquals(1, counter.inputCount());
        assertEquals(width, counter.outputCount());

        for (int i = 0; i < 1000; i++) {
            assertEquals(i, getOutput(counter, 0, width));
            counter.getIn(0).setWire(Wire.on());
            compute(counter);
            counter.getIn(0).setWire(Wire.off());
            compute(counter);
        }
    }

    @Test
    public void incrementer() {
        int width = 32;
        SimpleModule incrementer = factory.incrementer(width);
        assertEquals(width + 1, incrementer.inputCount());
        assertEquals(width, incrementer.outputCount());

        int value = 2345;
        incrementer.getIn(width).setWire(Wire.on());
        setInput(incrementer, 0, width, value);
        compute(incrementer);
        assertEquals(value + 1, getOutput(incrementer, 0, width));
    }

    @Test
    public void ram() {
        int width = 4;
        SimpleModule ram = factory.ram(width);
        assertEquals(2 * width + 1, ram.inputCount());
        assertEquals(width, ram.outputCount());
        int value = 13;
        int address = 5;
        setInput(ram, 0, width, value);
        setInput(ram, width, width, address);
        compute(ram);
        assertEquals(0, getOutput(ram, 0, width));

        ram.getIn(2 * width).setWire(Wire.on());
        compute(ram);
        assertEquals(value, getOutput(ram, 0, width));
    }

    @Test
    public void recursiveMultiplexer() {
        int width = 5;
        int depth = 3;
        int words = 1 << depth;
        SimpleModule mux = factory.multiplexer(width, depth);
        assertEquals(width * words + depth, mux.inputCount());
        assertEquals(width, mux.outputCount());

        for (int i = 0; i < words; i++) {
            setInput(mux, i * width, width, i);
        }
        for (int i = 0; i < words; i++) {
            setInput(mux, width * words, depth, i);
            compute(mux);
            assertEquals(i, getOutput(mux, 0, width));
        }
    }

    @Test
    public void recursiveDemultiplexer() {
        int width = 5;
        int depth = 3;
        int words = 1 << depth;
        SimpleModule demux = factory.demultiplexer(width, depth);
        assertEquals(width + depth, demux.inputCount());
        assertEquals(width * words, demux.outputCount());

        int input = 23;
        setInput(demux, 0, width, input);
        for (int i = 0; i < words; i++) {
            setInput(demux, width, depth, i);
            compute(demux);
            for (int j = 0; j < words; j++) {
                assertEquals(i == j ? input : 0, getOutput(demux, j * width, width));
            }
        }
    }

    @Test
    public void dFlipFlop() {
        SimpleModule flipFlop = factory.dGatedFlipFlop();
        assertEquals(2, flipFlop.inputCount());
        assertEquals(2, flipFlop.outputCount());

        compute(flipFlop);
        assertFalse(flipFlop.getOut(0).getSignal());
        assertTrue(flipFlop.getOut(1).getSignal());

        flipFlop.getIn(0).setWire(Wire.on());
        compute(flipFlop);
        assertFalse(flipFlop.getOut(0).getSignal());
        assertTrue(flipFlop.getOut(1).getSignal());

        flipFlop.getIn(1).setWire(Wire.on());
        compute(flipFlop);
        assertTrue(flipFlop.getOut(0).getSignal());
        assertFalse(flipFlop.getOut(1).getSignal());

        flipFlop.getIn(1).setWire(Wire.off());
        compute(flipFlop);
        assertTrue(flipFlop.getOut(0).getSignal());
        assertFalse(flipFlop.getOut(1).getSignal());

        flipFlop.getIn(0).setWire(Wire.off());
        compute(flipFlop);
        assertTrue(flipFlop.getOut(0).getSignal());
        assertFalse(flipFlop.getOut(1).getSignal());
    }

    @Test
    public void invertedSetResetFlipFlop() {
        SimpleModule flipFlop = factory.srNandFlipFlop();
        assertEquals(2, flipFlop.inputCount());
        assertEquals(2, flipFlop.outputCount());

        flipFlop.getIn(0).setWire(Wire.on());
        flipFlop.getIn(1).setWire(Wire.on());

        compute(flipFlop);
        assertFalse(flipFlop.getOut(0).getSignal());
        assertTrue(flipFlop.getOut(1).getSignal());

        flipFlop.getIn(0).setWire(Wire.off());
        compute(flipFlop);
        assertTrue(flipFlop.getOut(0).getSignal());
        assertFalse(flipFlop.getOut(1).getSignal());

        flipFlop.getIn(0).setWire(Wire.on());
        compute(flipFlop);
        assertTrue(flipFlop.getOut(0).getSignal());
        assertFalse(flipFlop.getOut(1).getSignal());

        flipFlop.getIn(1).setWire(Wire.off());
        compute(flipFlop);
        assertFalse(flipFlop.getOut(0).getSignal());
        assertTrue(flipFlop.getOut(1).getSignal());

        flipFlop.getIn(1).setWire(Wire.on());
        compute(flipFlop);
        assertFalse(flipFlop.getOut(0).getSignal());
        assertTrue(flipFlop.getOut(1).getSignal());
    }

    @Test
    public void rippleCarryAdder() {
        int width = 8;
        SimpleModule adder = factory.rippleCarryAdder(width);
        int a = 57;
        int b = 212;
        int carry = 1;
        adderTest(adder, width, a, b, carry);
    }

    private void adderTest(SimpleModule adder, int width, int a, int b, int carry) {
        assertEquals(2 * width + 1, adder.inputCount());
        assertEquals(width + 1, adder.outputCount());
        setInput(adder, 0, width, a);
        setInput(adder, width, width, b);
        setInput(adder, 2 * width, 1, carry);
        compute(adder);
        long result = getOutput(adder, 0, width + 1);
        assertEquals(a + b + carry, result);
    }

    @Test
    public void fullAdder() {
        SimpleModule fullAdder = factory.fullAdder();
        assertEquals(3, fullAdder.inputCount());
        assertEquals(2, fullAdder.outputCount());
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    fullAdder.getIn(0).setWire(a != 0 ? Wire.on() : Wire.off());
                    fullAdder.getIn(1).setWire(b != 0 ? Wire.on() : Wire.off());
                    fullAdder.getIn(2).setWire(c != 0 ? Wire.on() : Wire.off());
                    compute(fullAdder);
                    boolean sum = fullAdder.getOut(0).getSignal();
                    boolean carry = fullAdder.getOut(1).getSignal();
                    int value = a + b + c;
                    assertEquals(Integer.toString(value), (value & 1) != 0, sum);
                    assertEquals(Integer.toString(value), (value & 2) != 0, carry);
                }
            }
        }
    }

    @Test
    public void halfAdder() {
        SimpleModule halfAdder = factory.halfAdder();
        assertEquals(2, halfAdder.inputCount());
        assertEquals(2, halfAdder.outputCount());

        halfAdder.getIn(0).setWire(Wire.off());
        halfAdder.getIn(1).setWire(Wire.off());
        compute(halfAdder);
        assertFalse(halfAdder.getOut(0).getSignal());
        assertFalse(halfAdder.getOut(1).getSignal());

        halfAdder.getIn(0).setWire(Wire.on());
        halfAdder.getIn(1).setWire(Wire.off());
        compute(halfAdder);
        assertTrue(halfAdder.getOut(0).getSignal());
        assertFalse(halfAdder.getOut(1).getSignal());

        halfAdder.getIn(0).setWire(Wire.off());
        halfAdder.getIn(1).setWire(Wire.on());
        compute(halfAdder);
        assertTrue(halfAdder.getOut(0).getSignal());
        assertFalse(halfAdder.getOut(1).getSignal());

        halfAdder.getIn(0).setWire(Wire.on());
        halfAdder.getIn(1).setWire(Wire.on());
        compute(halfAdder);
        assertFalse(halfAdder.getOut(0).getSignal());
        assertTrue(halfAdder.getOut(1).getSignal());
    }

    @Test
    public void multiplexer() {
        SimpleModule multiplexer = factory.multiplexer();
        assertEquals(3, multiplexer.inputCount());
        assertEquals(1, multiplexer.outputCount());
        multiplexer.getIn(0).setWire(Wire.on());
        multiplexer.getIn(1).setWire(Wire.off());
        multiplexer.getIn(2).setWire(Wire.off());
        compute(multiplexer);
        assertTrue(multiplexer.getOut(0).getSignal());

        multiplexer.getIn(2).setWire(Wire.on());
        compute(multiplexer);
        assertFalse(multiplexer.getOut(0).getSignal());
    }

    @Test
    public void demultiplexer() {
        SimpleModule demultiplexer = factory.demultiplexer();
        assertEquals(2, demultiplexer.inputCount());
        assertEquals(2, demultiplexer.outputCount());
        demultiplexer.getIn(0).setWire(Wire.on());
        demultiplexer.getIn(1).setWire(Wire.off());
        compute(demultiplexer);
        assertTrue(demultiplexer.getOut(0).getSignal());
        assertFalse(demultiplexer.getOut(1).getSignal());

        demultiplexer.getIn(1).setWire(Wire.on());
        compute(demultiplexer);
        assertFalse(demultiplexer.getOut(0).getSignal());
        assertTrue(demultiplexer.getOut(1).getSignal());
    }

    @Test
    public void not() {
        SimpleModule not = factory.not();
        assertEquals(1, not.inputCount());
        assertEquals(1, not.outputCount());

        not.getIn(0).setWire(Wire.off());
        compute(not);
        assertTrue(not.getOut(0).getSignal());

        not.getIn(0).setWire(Wire.on());
        compute(not);
        assertFalse(not.getOut(0).getSignal());
    }

    @Test
    public void and() {
        SimpleModule and = factory.and();
        assertEquals(2, and.inputCount());
        assertEquals(1, and.outputCount());

        and.getIn(0).setWire(Wire.off());
        and.getIn(1).setWire(Wire.off());
        compute(and);
        assertFalse(and.getOut(0).getSignal());

        and.getIn(0).setWire(Wire.on());
        and.getIn(1).setWire(Wire.off());
        compute(and);
        assertFalse(and.getOut(0).getSignal());

        and.getIn(0).setWire(Wire.off());
        and.getIn(1).setWire(Wire.on());
        compute(and);
        assertFalse(and.getOut(0).getSignal());

        and.getIn(0).setWire(Wire.on());
        and.getIn(1).setWire(Wire.on());
        compute(and);
        assertTrue(and.getOut(0).getSignal());
    }

    @Test
    public void nand() {
        SimpleModule nand = factory.nand();
        assertEquals(2, nand.inputCount());
        assertEquals(1, nand.outputCount());

        nand.getIn(0).setWire(Wire.off());
        nand.getIn(1).setWire(Wire.off());
        compute(nand);
        assertTrue(nand.getOut(0).getSignal());

        nand.getIn(0).setWire(Wire.on());
        nand.getIn(1).setWire(Wire.off());
        compute(nand);
        assertTrue(nand.getOut(0).getSignal());

        nand.getIn(0).setWire(Wire.off());
        nand.getIn(1).setWire(Wire.on());
        compute(nand);
        assertTrue(nand.getOut(0).getSignal());

        nand.getIn(0).setWire(Wire.on());
        nand.getIn(1).setWire(Wire.on());
        compute(nand);
        assertFalse(nand.getOut(0).getSignal());
    }

    @Test
    public void or() {
        SimpleModule or = factory.or();
        assertEquals(2, or.inputCount());
        assertEquals(1, or.outputCount());

        or.getIn(0).setWire(Wire.off());
        or.getIn(1).setWire(Wire.off());
        compute(or);
        assertFalse(or.getOut(0).getSignal());

        or.getIn(0).setWire(Wire.on());
        or.getIn(1).setWire(Wire.off());
        compute(or);
        assertTrue(or.getOut(0).getSignal());

        or.getIn(0).setWire(Wire.off());
        or.getIn(1).setWire(Wire.on());
        compute(or);
        assertTrue(or.getOut(0).getSignal());

        or.getIn(0).setWire(Wire.on());
        or.getIn(1).setWire(Wire.on());
        compute(or);
        assertTrue(or.getOut(0).getSignal());
    }

    @Test
    public void xor() {
        SimpleModule xor = factory.xor();
        assertEquals(2, xor.inputCount());
        assertEquals(1, xor.outputCount());

        xor.getIn(0).setWire(Wire.off());
        xor.getIn(1).setWire(Wire.off());
        compute(xor);
        assertFalse(xor.getOut(0).getSignal());

        xor.getIn(0).setWire(Wire.on());
        xor.getIn(1).setWire(Wire.off());
        compute(xor);
        assertTrue(xor.getOut(0).getSignal());

        xor.getIn(0).setWire(Wire.off());
        xor.getIn(1).setWire(Wire.on());
        compute(xor);
        assertTrue(xor.getOut(0).getSignal());

        xor.getIn(0).setWire(Wire.on());
        xor.getIn(1).setWire(Wire.on());
        compute(xor);
        assertFalse(xor.getOut(0).getSignal());
    }

    @Test
    public void pulse() {
        SimpleModule pulse = new ModuleFactory().pulse();
        Wire out = pulse.getOut(0);

        Engine engine = new Engine();
        engine.activate(pulse.getGates());
        assertFalse(out.getSignal());
        engine.tick();
        assertTrue(out.getSignal());
        engine.tick();
        assertFalse(out.getSignal());
    }

    private void compute(SimpleModule mod) {
        Engine engine = new Engine();
        engine.activate(mod.getGates());
        int count = 0;
        while (engine.isActive()) {
            engine.tick();
            if (count++ > 1000) {
                throw new IllegalStateException();
            }
        }
    }

    private void setInput(SimpleModule mod, int offset, int width, long value) {
        WireReference[] inputs = mod.getInputs();
        for (int i = 0; i < width; i++) {
            inputs[offset + i].setWire(((value & 1) != 0) ? Wire.on() : Wire.off());
            value >>>= 1;
        }
    }

    private long getOutput(SimpleModule mod, int offset, int width) {
        Wire[] outputs = mod.getOutputs();
        long result = 0;
        for (int i = 0; i < width; i++) {
            if (outputs[offset + i].getSignal()) {
                result |= 1L << i;
            }
        }
        return result;
    }

    private void connect(SimpleModule source, int sourceIndex, SimpleModule destination, int destinationIndex, int width) {
        connect(outBus(source, sourceIndex, width), inBus(destination, destinationIndex, width));
    }

    private List<Wire> outBus(SimpleModule mod, int index, int width) {
        return Arrays.asList(mod.getOutputs()).subList(index, index + width);
    }

    private List<WireReference> inBus(SimpleModule mod, int index, int width) {
        return Arrays.asList(mod.getInputs()).subList(index, index + width);
    }

    private void connect(List<Wire> source, List<WireReference> destination) {
        assert source.size() == destination.size();
        for (int i = 0; i < source.size(); i++) {
            destination.get(i).setWire(source.get(i));
        }
    }

}
