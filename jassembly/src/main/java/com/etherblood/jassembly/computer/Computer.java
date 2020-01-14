package com.etherblood.jassembly.computer;

import com.etherblood.jassembly.core.Engine;
import static com.etherblood.jassembly.usability.BusUtil.*;
import com.etherblood.jassembly.core.Wire;
import com.etherblood.jassembly.usability.Util;
import com.etherblood.jassembly.usability.code.ControlSignals;
import com.etherblood.jassembly.usability.code.MachineInstruction;
import com.etherblood.jassembly.usability.code.MachineInstructionSet;
import com.etherblood.jassembly.usability.modules.MemoryModule;
import com.etherblood.jassembly.usability.modules.ModuleFactory;
import com.etherblood.jassembly.usability.modules.SimpleModule;
import com.etherblood.jassembly.usability.monitoring.CycleStats;
import com.etherblood.jassembly.usability.signals.SignalRange;
import com.etherblood.jassembly.usability.signals.WireReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class Computer {

    public final MemoryModule ram;
    public final SimpleModule adder;
    public final SimpleModule rshift;
    public final SimpleModule lu;

    public final MemoryModule instruction;
    public final SimpleModule commandMux;

    public final SimpleModule noop;
    public final MemoryModule ax;
    public final MemoryModule bx, cx, sb, sp;
    public final MemoryModule pc;
    public final SimpleModule pcInc;
    public final SimpleModule pcMux;

    public final SimpleModule busMuxRead0;
    public final SimpleModule busDemuxRead0;
    public final SimpleModule busMuxRead1;
    public final SimpleModule busDemuxRead1;
    public final SimpleModule busMuxWrite;
    public final SimpleModule busDemuxWrite;
    public final SimpleModule opArgDemux;

    public final SimpleModule modRead0;
    public final SimpleModule modWrite;

    public final SimpleModule commandDecodeMux;
    public final SimpleModule ramAnd;

    public final Wire clockWire = Wire.mutable(false);
    public final Wire writeWire = Wire.mutable(false);
    
    private final MachineInstructionSet instructionSet;

    public Computer(int width, List<Integer> program, int ramWords, MachineInstructionSet instructionSet) {
        this.instructionSet = instructionSet;
        if(Integer.bitCount(width) != 1) {
            throw new IllegalArgumentException("wordsize must be a power of 2.");
        }
        if(1 << width < ramWords) {
            throw new IllegalArgumentException("wordsize too small to address full RAM.");
        }
        if(1 << width < instructionSet.instructionCount()) {
            throw new IllegalArgumentException("wordsize too small to address instructionSet.");
        }
        int depth = Util.floorLog(width);
        int registersAddressWidth = ControlSignals.R0_ADR.length;
        int operatorsAddressWidth = ControlSignals.OP_ADR.length;
        ModuleFactory factory = new ModuleFactory();

        ram = factory.ram(width, ramWords);
        adder = factory.rippleCarryAdder(width);
        rshift = factory.logicalRightShift(width, depth);
        lu = factory.logicUnit(width);

        noop = factory.noop(width + 1, width);
        ax = factory.msFlipFlop(width);
        bx = factory.msFlipFlop(width);
        cx = factory.msFlipFlop(width);
        sb = factory.msFlipFlop(width);
        sp = factory.msFlipFlop(width);
        pc = factory.msFlipFlop(width);
        pcInc = factory.incrementer(width);
        pcMux = factory.multiplexer(width);
        instruction = factory.msFlipFlop(width);
        commandMux = factory.multiplexer(width);
        commandDecodeMux = factory.mux(ControlSignals.SIGNAL_BITS, instructionSet.instructionCount());

        busMuxRead0 = factory.multiplexer(width, registersAddressWidth);
        busDemuxRead0 = factory.demultiplexer(width, operatorsAddressWidth);
        busMuxRead1 = factory.multiplexer(width, registersAddressWidth);
        busDemuxRead1 = factory.demultiplexer(width, operatorsAddressWidth);
        busMuxWrite = factory.multiplexer(width, operatorsAddressWidth);
        busDemuxWrite = factory.demultiplexer(width + 1, registersAddressWidth);
        opArgDemux = factory.demultiplexer(ControlSignals.OP_ARG.length, operatorsAddressWidth);
        modRead0 = factory.signalModifier(width);
        modWrite = factory.signalModifier(width);
        ramAnd = factory.and();

        for (int i = 0; i < ramWords; i++) {
            ram.getSignals().subRange(i * width, width).set(instructionSet.codeByInstruction(instructionSet.map().noop()));
        }
        int nextLine = 0;
        for (int lineCode : program) {
            ram.getSignals().subRange(nextLine++ * width, width).set(lineCode);
        }
        instruction.getSignals().set(instructionSet.codeByInstruction(instructionSet.map().readInstruction()));
        sp.getSignals().set(ramWords - 1);
        sb.getSignals().set(ramWords - 1);

        //TODO: below is workaround for stability issues...
        Engine engine = new Engine();
        quiet(engine);
        connectWires(width, depth, instructionSet);
        engine.activate(clockWire);
        quiet(engine);
    }

    private void connectWires(int width, int depth, MachineInstructionSet instructionSet) {
        int registersAddressWidth = ControlSignals.R0_ADR.length;
        int operatorsAddressWidth = ControlSignals.OP_ADR.length;
        List<WireReference>[] registerInputs = new List[]{
            inBus(noop, 0, width + 1),
            inBus(pcMux, width, width + 1),
            inBus(commandMux, width, width + 1),
            inBus(ax, 0, width + 1),
            inBus(bx, 0, width + 1),
            inBus(cx, 0, width + 1),
            inBus(sb, 0, width + 1),
            inBus(sp, 0, width + 1)
        };
        List<Wire>[] registerOutputs = new List[]{
            outBus(noop, 0, width),
            outBus(pc, 0, width),
            outBus(instruction, 0, width),
            outBus(ax, 0, width),
            outBus(bx, 0, width),
            outBus(cx, 0, width),
            outBus(sb, 0, width),
            outBus(sp, 0, width)
        };
        List<WireReference>[] operatorInputsA = new List[]{
            inBus(lu, 0, width),
            inBus(adder, 0, width),
            inBus(rshift, 0, width),
            inBus(ram, 0, width)
        };
        List<WireReference>[] operatorInputsB = new List[]{
            inBus(lu, width, width),
            inBus(adder, width, width),
            inBus(rshift, width, depth),
            inBus(ram, width, width)
        };
        List<Wire>[] operatorOutputs = new List[]{
            outBus(lu, 0, width),
            outBus(adder, 0, width),
            outBus(rshift, 0, width),
            outBus(ram, 0, width)
        };

        List<WireReference> controlSignals = new ArrayList<>();
        controlSignals.add(pcInc.getIn(width));
        controlSignals.addAll(inBus(busMuxRead0, (1 << ControlSignals.R0_ADR.length) * width, registersAddressWidth));
        controlSignals.addAll(inBus(busMuxRead1, (1 << ControlSignals.R0_ADR.length) * width, registersAddressWidth));
        controlSignals.addAll(inBus(busDemuxWrite, width + 1, registersAddressWidth));
        controlSignals.addAll(combineBuses(
                inBus(busDemuxRead0, width, operatorsAddressWidth),
                inBus(busDemuxRead1, width, operatorsAddressWidth),
                inBus(busMuxWrite, (1 << ControlSignals.OP_ADR.length) * width, operatorsAddressWidth),
                inBus(opArgDemux, ControlSignals.OP_ARG.length, operatorsAddressWidth)));
        controlSignals.addAll(Arrays.asList(opArgDemux.getInputs()).subList(0, ControlSignals.OP_ARG.length));
        controlSignals.addAll(inBus(modRead0, width, ControlSignals.R0_MOD.length));
        controlSignals.addAll(inBus(modWrite, width, ControlSignals.W_MOD.length));
        assert controlSignals.size() == ControlSignals.SIGNAL_BITS;

        ramAnd.getIn(0).setWire(writeWire);
        ramAnd.getIn(1).setWire(opArgDemux.getOut(ControlSignals.RAM_ADR * ControlSignals.OP_ARG.length));
        ram.getIn(2 * width).setWire(ramAnd.getOut(0));
        adder.getIn(2 * width).setWire(opArgDemux.getOut(ControlSignals.ADD_ADR * ControlSignals.OP_ARG.length));
        lu.getIn(2 * width).setWire(opArgDemux.getOut(ControlSignals.LU_ADR * ControlSignals.OP_ARG.length));
        lu.getIn(2 * width + 1).setWire(opArgDemux.getOut(ControlSignals.LU_ADR * ControlSignals.OP_ARG.length + 1));

        connect(commandMux, 0, instruction, 0, width);
        setInput(commandMux, 0, width, instructionSet.codeByInstruction(instructionSet.map().readInstruction()));
        connect(pcInc, 0, pc, 0, width);
        connect(pc, 0, pcMux, 0, width);
        connect(pcMux, 0, pcInc, 0, width);

        connect(busMuxRead0, 0, modRead0, 0, width);
        connect(modRead0, 0, busDemuxRead0, 0, width);
        connect(busMuxRead1, 0, busDemuxRead1, 0, width);
        connect(busMuxWrite, 0, modWrite, 0, width);
        connect(modWrite, 0, busDemuxWrite, 0, width);
        busDemuxWrite.getIn(width).setWire(clockWire);

        for (int i = 0; i < registerInputs.length; i++) {
            List<WireReference> registerInput = registerInputs[i];
            connect(outBus(busDemuxWrite, i * (width + 1), registerInput.size()), registerInput);
        }
        for (int i = 0; i < registerOutputs.length; i++) {
            connect(registerOutputs[i], inBus(busMuxRead0, i * width, width));
            connect(registerOutputs[i], inBus(busMuxRead1, i * width, width));
        }
        for (int i = 0; i < operatorInputsA.length; i++) {
            List<WireReference> operator = operatorInputsA[i];
            connect(outBus(busDemuxRead0, i * width, operator.size()), operator);
        }
        for (int i = 0; i < operatorInputsB.length; i++) {
            List<WireReference> operator = operatorInputsB[i];
            connect(outBus(busDemuxRead1, i * width, operator.size()), operator);
        }
        for (int i = 0; i < operatorOutputs.length; i++) {
            List<Wire> operator = operatorOutputs[i];
            connect(operator, inBus(busMuxWrite, i * width, operator.size()));
        }

        connect(instruction, 0, commandDecodeMux, instructionSet.instructionCount() * ControlSignals.SIGNAL_BITS, Util.ceilLog(instructionSet.instructionCount()));
        for (int i = 0; i < controlSignals.size(); i++) {
            controlSignals.get(i).setWire(commandDecodeMux.getOut(i));
        }
        for (int i = 0; i < instructionSet.instructionCount(); i++) {
            MachineInstruction instruction = instructionSet.instructionByCode(i);
            setInput(commandDecodeMux, i * ControlSignals.SIGNAL_BITS, ControlSignals.SIGNAL_BITS, instruction.getControlFlags());
        }

        pc.getIn(width).setWire(clockWire);
        instruction.getIn(width).setWire(clockWire);
    }

    private List<SimpleModule> modules() {
        return Arrays.asList(ram, adder, ax, pc, pcInc, pcMux, instruction, commandDecodeMux, commandMux, noop, bx, cx, sb, sp, busDemuxRead0, busDemuxRead1, busDemuxWrite, busMuxRead0, busMuxRead1, busMuxWrite, modRead0, modWrite, rshift, lu, opArgDemux, ramAnd);
    }

    private void quiet(Engine engine) {
        for (SimpleModule module : modules()) {
            engine.activate(quiet(module).getGates());
        }
    }

    public int countGates() {
        return modules().stream().mapToInt(x -> x.getGates().length).sum();
    }

    private static <T extends SimpleModule> T quiet(T mod) {//probably obsolete?
        //workaround for potentially unstable initial values
        Engine engine = new Engine();
        engine.activate(mod.getGates());
        while (engine.isActive()) {
            engine.tick();
        }
        return mod;
    }

    public CycleStats advanceCycle(Engine engine) {
        CycleStats stats = new CycleStats(currentInstruction());
        clockWire.setSignal(true);
        engine.activate(clockWire);
        while (engine.isActive()) {
            stats.getTicks().add(engine.monitoredTick());
        }

        writeWire.setSignal(true);
        engine.activate(writeWire);
        while (engine.isActive()) {
            stats.getTicks().add(engine.monitoredTick());
        }
        writeWire.setSignal(false);
        engine.activate(writeWire);
        while (engine.isActive()) {
            stats.getTicks().add(engine.monitoredTick());
        }

        clockWire.setSignal(false);
        engine.activate(clockWire);
        while (engine.isActive()) {
            stats.getTicks().add(engine.monitoredTick());
        }
        return stats;
    }

    public void printState() {
        SignalRange currentCommand = instruction.getSignals();
        System.out.println("ram: " + ram.getSignals().toHexStrig());
        System.out.println("pc: " + pc.getSignals().toHexStrig() + " (" + pc.getSignals().getAsLong() + ")");
        System.out.println("ix: " + currentCommand.toHexStrig() + " (" + instructionSet.instructionByCode(Math.toIntExact(currentCommand.getAsLong())) + ")");
        System.out.println("ax: " + ax.getSignals().toHexStrig() + " (" + ax.getSignals().getAsLong() + ")");
        System.out.println("bx: " + bx.getSignals().toHexStrig() + " (" + bx.getSignals().getAsLong() + ")");
        System.out.println("cx: " + cx.getSignals().toHexStrig() + " (" + cx.getSignals().getAsLong() + ")");
        System.out.println("sb: " + sb.getSignals().toHexStrig() + " (" + sb.getSignals().getAsLong() + ")");
        System.out.println("sp: " + sp.getSignals().toHexStrig() + " (" + sp.getSignals().getAsLong() + ")");
        System.out.println();
    }
    
    private MachineInstruction currentInstruction() {
        SignalRange currentCommand = instruction.getSignals();
        return instructionSet.instructionByCode(Math.toIntExact(currentCommand.getAsLong()));
    }
}
