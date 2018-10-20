package com.etherblood.circuit.computer;

import com.etherblood.circuit.core.Engine;
import com.etherblood.circuit.core.Wire;
import com.etherblood.circuit.usability.modules.MemoryModule;
import com.etherblood.circuit.usability.modules.ModuleFactory;
import com.etherblood.circuit.usability.modules.ModuleUtil;
import com.etherblood.circuit.usability.signals.SignalRange;
import com.etherblood.circuit.usability.modules.SimpleModule;
import com.etherblood.circuit.usability.signals.WireReference;
import com.etherblood.circuit.usability.codes.Command;
import com.etherblood.circuit.usability.codes.ControlSignals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ModuleFactory factory = new ModuleFactory();
        int width = 8;
        MemoryModule ram = factory.ram(width);
        SimpleModule adder = factory.rippleCarryAdder(width);

        MemoryModule command = factory.msFlipFlop(width);
        SimpleModule commandMux = factory.multiplexer(width);

        SimpleModule noop = factory.noop(width + 1, width);
        MemoryModule acc = factory.msFlipFlop(width);
        MemoryModule x0 = factory.msFlipFlop(width);
        MemoryModule x1 = factory.msFlipFlop(width);
        MemoryModule x2 = factory.msFlipFlop(width);
        MemoryModule x3 = factory.msFlipFlop(width);
        MemoryModule pc = factory.msFlipFlop(width);
        SimpleModule pcInc = factory.incrementer(width);
        SimpleModule pcMux = factory.multiplexer(width);

        Wire clockWire = Wire.off();

        List<WireReference>[] registerInputs = new List[]{
            inBus(noop, 0, width + 1),
            inBus(pcMux, width, width + 1),
            inBus(commandMux, width, width + 1),
            inBus(acc, 0, width + 1),
            inBus(x0, 0, width + 1),
            inBus(x1, 0, width + 1),
            inBus(x2, 0, width + 1),
            inBus(x3, 0, width + 1)
        };
        List<Wire>[] registerOutputs = new List[]{
            outBus(noop, 0, width),
            outBus(pc, 0, width),
            outBus(command, 0, width),
            outBus(acc, 0, width),
            outBus(x0, 0, width),
            outBus(x1, 0, width),
            outBus(x2, 0, width),
            outBus(x3, 0, width)
        };
        List<WireReference>[] operatorInputsA = new List[]{
            inBus(ram, 0, width),
            inBus(adder, 0, width)
        };
        List<WireReference>[] operatorInputsB = new List[]{
            inBus(ram, width, width),
            inBus(adder, width, width)
        };
        List<Wire>[] operatorOutputs = new List[]{
            outBus(ram, 0, width),
            outBus(adder, 0, width)
        };

        int registersAddressWidth = 32 - Integer.numberOfLeadingZeros(registerInputs.length - 1);
        int operatorsAddressWidth = 32 - Integer.numberOfLeadingZeros(operatorInputsA.length - 1);

        SimpleModule busMuxRead0 = factory.multiplexer(width, registersAddressWidth);
        SimpleModule busDemuxRead0 = factory.demultiplexer(width, operatorsAddressWidth);
        SimpleModule busMuxRead1 = factory.multiplexer(width, registersAddressWidth);
        SimpleModule busDemuxRead1 = factory.demultiplexer(width, operatorsAddressWidth);
        SimpleModule busMuxWrite = factory.multiplexer(width, operatorsAddressWidth);
        SimpleModule busDemuxWrite = factory.demultiplexer(width + 1, registersAddressWidth);
        SimpleModule opArgDemux = factory.demultiplexer(1, operatorsAddressWidth);

        SimpleModule modRead0 = factory.signalModifier(width);
        SimpleModule modWrite = factory.signalModifier(width);
        
        adder.getIn(2 * width).setWire(opArgDemux.getOut(1));

        List<WireReference> controlSignals = new ArrayList<>();
        controlSignals.add(pcInc.getIn(width));
        controlSignals.addAll(inBus(busMuxRead0, registerOutputs.length * width, registersAddressWidth));
        controlSignals.addAll(inBus(busMuxRead1, registerOutputs.length * width, registersAddressWidth));
        controlSignals.addAll(inBus(busDemuxWrite, width + 1, registersAddressWidth));
        controlSignals.addAll(combineFour(
                inBus(busDemuxRead0, width, operatorsAddressWidth),
                inBus(busDemuxRead1, width, operatorsAddressWidth),
                inBus(busMuxWrite, operatorOutputs.length * width, operatorsAddressWidth),
                inBus(opArgDemux, 1, operatorsAddressWidth)));
        controlSignals.add(opArgDemux.getIn(0));
        controlSignals.addAll(inBus(modRead0, width, 2));
        controlSignals.addAll(inBus(modWrite, width, 2));
        assert controlSignals.size() <= ControlSignals.SIGNAL_BITS;

        SimpleModule commandDecodeMux = factory.multiplexer(ControlSignals.SIGNAL_BITS, width);
        Command[] commands = Command.values();

        for (int i = 0; i < 1 << width; i++) {
            ram.getSignals().subRange(i * width, width).set(Command.WAIT.ordinal());
        }
        int nextLine = 0;
        ram.getSignals().subRange(nextLine++ * width, width).set(Command.LOAD_CONST.ordinal());
        ram.getSignals().subRange(nextLine++ * width, width).set(3);
        ram.getSignals().subRange(nextLine++ * width, width).set(Command.TO_X0.ordinal());
        ram.getSignals().subRange(nextLine++ * width, width).set(Command.LOAD_CONST.ordinal());
        ram.getSignals().subRange(nextLine++ * width, width).set(17);
        ram.getSignals().subRange(nextLine++ * width, width).set(Command.SUB_X0.ordinal());
        ram.getSignals().subRange(nextLine++ * width, width).set(Command.JUMP.ordinal());
        command.getSignals().set(Command.LOAD_CMD.ordinal());

        Engine engine = new Engine();
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
        engine.activate(quiet(x3).getGates());
        engine.activate(quiet(busDemuxRead0).getGates());
        engine.activate(quiet(busDemuxRead1).getGates());
        engine.activate(quiet(busDemuxWrite).getGates());
        engine.activate(quiet(busMuxRead0).getGates());
        engine.activate(quiet(busMuxRead1).getGates());
        engine.activate(quiet(busMuxWrite).getGates());
        engine.activate(quiet(modRead0).getGates());
        engine.activate(quiet(modWrite).getGates());

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

        connect(command, 0, commandDecodeMux, (1 << width) * ControlSignals.SIGNAL_BITS, width);
        for (int i = 0; i < controlSignals.size(); i++) {
            controlSignals.get(i).setWire(commandDecodeMux.getOut(i));
        }
        for (int i = 0; i < 1 << width; i++) {
            Command cmd = i < commands.length ? commands[i] : Command.UNDEFINED;
            setInput(commandDecodeMux, i * ControlSignals.SIGNAL_BITS, ControlSignals.SIGNAL_BITS, cmd.getControlSignals());
        }

        pc.getIn(width).setWire(clockWire);
        command.getIn(width).setWire(clockWire);

        engine.activate(clockWire);
        while (engine.isActive()) {
            engine.tick();
        }
        for (int i = 0; i < 12; i++) {
            System.out.println("ram: " + ram.getSignals().toHexStrig());
            SignalRange currentCommand = command.getSignals();
            System.out.println("cmd: " + currentCommand.toHexStrig() + " (" + commands[(int) currentCommand.getAsLong()] + ")");
            System.out.println("pc: " + pc.getSignals().toHexStrig());
            System.out.println("ac: " + acc.getSignals().toHexStrig());
            System.out.println("x0: " + x0.getSignals().toHexStrig());
            System.out.println("x1: " + x1.getSignals().toHexStrig());
            System.out.println("x2: " + x2.getSignals().toHexStrig());
            System.out.println("x3: " + x3.getSignals().toHexStrig());
            System.out.println();
            clockWire.setSignal(!clockWire.getSignal());
            engine.activate(clockWire);
            while (engine.isActive()) {
                engine.tick();
            }
            clockWire.setSignal(!clockWire.getSignal());
            engine.activate(clockWire);
            while (engine.isActive()) {
                engine.tick();
            }
        }
    }

    private static <T extends SimpleModule> T quiet(T mod) {
        //workaround for potentially unstable initial values
        Engine engine = new Engine();
        engine.activate(mod.getGates());
        while (engine.isActive()) {
            engine.tick();
        }
        return mod;
    }

    private static List<WireReference> combineFour(List<WireReference>... ref) {
        List<WireReference> result = new ArrayList<>();
        int len = ref[0].size();
        for (int i = 0; i < len; i++) {
            result.add(ModuleUtil.combine(ref[0].get(i), ref[1].get(i), ref[2].get(i), ref[3].get(i)));
        }
        return result;
    }

    private static long getInput(SimpleModule mod, int offset, int width) {
        WireReference[] inputs = mod.getInputs();
        long result = 0;
        for (int i = 0; i < width; i++) {
            if (inputs[offset + i].getWire().getSignal()) {
                result |= 1L << i;
            }
        }
        return result;
    }

    private static void setInput(SimpleModule mod, int offset, int width, long value) {
        WireReference[] inputs = mod.getInputs();
        for (int i = 0; i < width; i++) {
            inputs[offset].setWire(((value & 1) != 0) ? Wire.on() : Wire.off());
            offset++;
            value >>>= 1;
        }
    }

    private static long getOutput(SimpleModule mod, int offset, int width) {
        Wire[] outputs = mod.getOutputs();
        long result = 0;
        for (int i = 0; i < width; i++) {
            if (outputs[offset + i].getSignal()) {
                result |= 1L << i;
            }
        }
        return result;
    }

    private static void connect(SimpleModule source, int sourceIndex, SimpleModule destination, int destinationIndex, int width) {
        connect(outBus(source, sourceIndex, width), inBus(destination, destinationIndex, width));
    }

    private static List<Wire> outBus(SimpleModule mod, int index, int width) {
        return Arrays.asList(mod.getOutputs()).subList(index, index + width);
    }

    private static List<WireReference> inBus(SimpleModule mod, int index, int width) {
        return Arrays.asList(mod.getInputs()).subList(index, index + width);
    }

    private static void connect(List<Wire> source, List<WireReference> destination) {
        assert source.size() == destination.size();
        for (int i = 0; i < source.size(); i++) {
            destination.get(i).setWire(source.get(i));
        }
    }

}
