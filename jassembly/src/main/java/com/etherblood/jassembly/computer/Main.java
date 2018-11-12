package com.etherblood.jassembly.computer;

import com.etherblood.jassembly.compile.SimpleCompiler;
import com.etherblood.jassembly.core.Engine;
import com.etherblood.jassembly.usability.signals.SignalRange;
import com.etherblood.jassembly.usability.code.Instruction;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String sourceFile = "fibonacci.txt";
        String sampleCode = readFile(sourceFile);
        List<Integer> program = new SimpleCompiler().compile(sampleCode);

        int width = 8;
        int ram = 256;
        Computer computer = new Computer(width, program, ram);
        System.out.println("Computer built from " + computer.countGates() + " nands.");
        System.out.println();
        printState(computer);
        Engine engine = new Engine();
        while (computer.instruction.getSignals().getAsLong() != Instruction.TERMINATE.ordinal()) {
            advanceCycle(computer, engine);
            printState(computer);
        }
    }

    private static String readFile(String sourceFile) throws IOException, URISyntaxException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        String sampleCode = new String(Files.readAllBytes(new File(classloader.getResource(sourceFile).toURI()).toPath()));
        return sampleCode;
    }

    private static void advanceCycle(Computer computer, Engine engine) {
        computer.clockWire.setSignal(true);
        engine.activate(computer.clockWire);
        while (engine.isActive()) {
            engine.tick();
        }

        computer.writeWire.setSignal(true);
        engine.activate(computer.writeWire);
        while (engine.isActive()) {
            engine.tick();
        }
        computer.writeWire.setSignal(false);
        engine.activate(computer.writeWire);
        while (engine.isActive()) {
            engine.tick();
        }

        computer.clockWire.setSignal(false);
        engine.activate(computer.clockWire);
        while (engine.isActive()) {
            engine.tick();
        }
    }

    private static void printState(Computer computer) {
        SignalRange currentCommand = computer.instruction.getSignals();
        System.out.println("ram: " + computer.ram.getSignals().toHexStrig());
        System.out.println("pc: " + computer.pc.getSignals().toHexStrig() + " (" + computer.pc.getSignals().getAsLong() + ")");
        System.out.println("ix: " + currentCommand.toHexStrig() + " (" + Instruction.values()[(int) currentCommand.getAsLong()] + ")");
        System.out.println("ax: " + computer.ax.getSignals().toHexStrig() + " (" + computer.ax.getSignals().getAsLong() + ")");
        System.out.println("bx: " + computer.bx.getSignals().toHexStrig() + " (" + computer.bx.getSignals().getAsLong() + ")");
        System.out.println("cx: " + computer.cx.getSignals().toHexStrig() + " (" + computer.cx.getSignals().getAsLong() + ")");
        System.out.println("sb: " + computer.sb.getSignals().toHexStrig() + " (" + computer.sb.getSignals().getAsLong() + ")");
        System.out.println("sp: " + computer.sp.getSignals().toHexStrig() + " (" + computer.sp.getSignals().getAsLong() + ")");
        System.out.println();
    }

}
