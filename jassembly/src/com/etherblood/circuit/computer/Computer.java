package com.etherblood.circuit.computer;

import com.etherblood.circuit.core.Engine;
import static com.etherblood.circuit.usability.BusUtil.*;
import com.etherblood.circuit.core.Wire;
import com.etherblood.circuit.usability.Util;
import com.etherblood.circuit.usability.codes.Command;
import com.etherblood.circuit.usability.codes.ControlSignals;
import com.etherblood.circuit.usability.modules.MemoryModule;
import com.etherblood.circuit.usability.modules.ModuleFactory;
import com.etherblood.circuit.usability.modules.SimpleModule;
import com.etherblood.circuit.usability.signals.WireReference;
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

    public final MemoryModule command;
    public final SimpleModule commandMux;

    public final SimpleModule noop;
    public final MemoryModule acc;
    public final MemoryModule x0, x1, x2, sp;
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

    public final Wire clockWire = Wire.off();
    public final Wire writeWire = Wire.off();

    public Computer(int width, List<Integer> program, int maxRam) {
        assert Integer.bitCount(width) == 1;
        int depth = Util.floorLog(width);
        int registersAddressWidth = ControlSignals.R0_ADR.length;
        int operatorsAddressWidth = ControlSignals.OP_ADR.length;
        ModuleFactory factory = new ModuleFactory();

        ram = factory.ram(width, maxRam);
        adder = factory.rippleCarryAdder(width);
        rshift = factory.logicalRightShift(width, depth);
        lu = factory.logicUnit(width);

        noop = factory.noop(width + 1, width);
        acc = factory.msFlipFlop(width);
        x0 = factory.msFlipFlop(width);
        x1 = factory.msFlipFlop(width);
        x2 = factory.msFlipFlop(width);
        sp = factory.msFlipFlop(width);
        pc = factory.msFlipFlop(width);
        pcInc = factory.incrementer(width);
        pcMux = factory.multiplexer(width);
        command = factory.msFlipFlop(width);
        commandMux = factory.multiplexer(width);
        commandDecodeMux = factory.mux(ControlSignals.SIGNAL_BITS, Command.values().length);

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

        for (int i = 0; i < maxRam; i++) {
            ram.getSignals().subRange(i * width, width).set(Command.WAIT.ordinal());
        }
        int nextLine = 0;
        for (int lineCode : program) {
            ram.getSignals().subRange(nextLine++ * width, width).set(lineCode);
        }
        command.getSignals().set(Command.LOAD_CMD.ordinal());
        sp.getSignals().set(maxRam);

        //TODO: below is workaround for stability issues...
        Engine engine = new Engine();
        quiet(engine);
        connectWires(width, depth);
        engine.activate(clockWire);
        quiet(engine);
    }

    private void connectWires(int width, int depth) {
        Command[] commands = Command.values();
        int registersAddressWidth = ControlSignals.R0_ADR.length;
        int operatorsAddressWidth = ControlSignals.OP_ADR.length;
        List<WireReference>[] registerInputs = new List[]{
            inBus(noop, 0, width + 1),
            inBus(pcMux, width, width + 1),
            inBus(commandMux, width, width + 1),
            inBus(acc, 0, width + 1),
            inBus(x0, 0, width + 1),
            inBus(x1, 0, width + 1),
            inBus(x2, 0, width + 1),
            inBus(sp, 0, width + 1)
        };
        List<Wire>[] registerOutputs = new List[]{
            outBus(noop, 0, width),
            outBus(pc, 0, width),
            outBus(command, 0, width),
            outBus(acc, 0, width),
            outBus(x0, 0, width),
            outBus(x1, 0, width),
            outBus(x2, 0, width),
            outBus(sp, 0, width)
        };
        List<WireReference>[] operatorInputsA = new List[]{
            inBus(ram, 0, width),
            inBus(adder, 0, width),
            inBus(rshift, 0, width),
            inBus(lu, 0, width)
        };
        List<WireReference>[] operatorInputsB = new List[]{
            inBus(ram, width, width),
            inBus(adder, width, width),
            inBus(rshift, width, depth),
            inBus(lu, width, width)
        };
        List<Wire>[] operatorOutputs = new List[]{
            outBus(ram, 0, width),
            outBus(adder, 0, width),
            outBus(rshift, 0, width),
            outBus(lu, 0, width)
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

        connect(commandMux, 0, command, 0, width);
        setInput(commandMux, 0, width, Command.LOAD_CMD.ordinal());
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

        connect(command, 0, commandDecodeMux, commands.length * ControlSignals.SIGNAL_BITS, Util.ceilLog(commands.length));
        for (int i = 0; i < controlSignals.size(); i++) {
            controlSignals.get(i).setWire(commandDecodeMux.getOut(i));
        }
        for (int i = 0; i < commands.length; i++) {
            Command cmd = commands[i];
            setInput(commandDecodeMux, i * ControlSignals.SIGNAL_BITS, ControlSignals.SIGNAL_BITS, cmd.getControlSignals());
        }

        pc.getIn(width).setWire(clockWire);
        command.getIn(width).setWire(clockWire);
    }

    private void quiet(Engine engine) {
        engine.activate(quiet(ram).getGates());
        engine.activate(quiet(adder).getGates());
        engine.activate(quiet(acc).getGates());
        engine.activate(quiet(pc).getGates());
        engine.activate(quiet(pcInc).getGates());
        engine.activate(quiet(pcMux).getGates());
        engine.activate(quiet(command).getGates());
        engine.activate(quiet(commandDecodeMux).getGates());
        engine.activate(quiet(commandMux).getGates());
        engine.activate(quiet(noop).getGates());
        engine.activate(quiet(x0).getGates());
        engine.activate(quiet(x1).getGates());
        engine.activate(quiet(x2).getGates());
        engine.activate(quiet(sp).getGates());
        engine.activate(quiet(busDemuxRead0).getGates());
        engine.activate(quiet(busDemuxRead1).getGates());
        engine.activate(quiet(busDemuxWrite).getGates());
        engine.activate(quiet(busMuxRead0).getGates());
        engine.activate(quiet(busMuxRead1).getGates());
        engine.activate(quiet(busMuxWrite).getGates());
        engine.activate(quiet(modRead0).getGates());
        engine.activate(quiet(modWrite).getGates());
        engine.activate(quiet(rshift).getGates());
        engine.activate(quiet(lu).getGates());
        engine.activate(quiet(opArgDemux).getGates());
        engine.activate(quiet(ramAnd).getGates());
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
}
