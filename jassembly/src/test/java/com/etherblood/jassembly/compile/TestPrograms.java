package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.computer.Computer;
import com.etherblood.jassembly.core.Engine;
import com.etherblood.jassembly.usability.codes.Command;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philipp
 */
public class TestPrograms {
    
    private final SimpleCompiler compiler = new SimpleCompiler();

    @Test
    public void _12x9() {
        List<Integer> program = compiler.compile(loadFile("12x9.txt"));
        long result = compute(program, 8, program.size() + 10);
        assertEquals(108, result);
    }
    
    @Test
    public void _3x5() {
        List<Integer> program = compiler.compile(loadFile("3x5.txt"));
        long result = compute(program, 8, program.size() + 20);
        assertEquals(15, result);
    }
    
    @Test
    public void _128x2000() {
        List<Integer> program = compiler.compile(loadFile("128x2000.txt"));
        long result = compute(program, 16, program.size() + 20);
        assertEquals(59392, result);
    }

    @Test
    public void fibonacci() {
        List<Integer> program = compiler.compile(loadFile("fibonacci.txt"));
        long result = compute(program, 8, program.size() + 50);
        assertEquals(55, result);
    }

    @Test
    public void relational() {
        List<Integer> program = compiler.compile(loadFile("relational.txt"));
        long result = compute(program, 16, program.size() + 20);
        assertEquals(1, result);
    }
    
    private static long compute(List<Integer> program, int width, int ram) {
        Computer computer = new Computer(width, program, ram);
        Engine engine = new Engine();
        while (computer.command.getSignals().getAsLong() != Command.TERMINATE.ordinal()) {
            advanceCycle(computer, engine);
        }
        return computer.acc.getSignals().getAsLong();
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

    private static String loadFile(String filename) {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            return new String(Files.readAllBytes(new File(classloader.getResource(filename).toURI()).toPath()));
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
