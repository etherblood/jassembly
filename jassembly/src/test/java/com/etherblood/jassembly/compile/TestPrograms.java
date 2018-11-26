package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.computer.Computer;
import com.etherblood.jassembly.core.Engine;
import com.etherblood.jassembly.usability.code.DefaultMachineInstructionSet;
import com.etherblood.jassembly.usability.code.MachineInstructionSet;
import com.etherblood.jassembly.usability.code.MachineInstructions;
import com.etherblood.jassembly.usability.monitoring.ProgramStats;
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
    private final MachineInstructions mapping = new MachineInstructions();
    private final MachineInstructionSet instructionSet = new DefaultMachineInstructionSet(mapping);

    @Test
    public void _12x9() {
        String name = "12x9.txt";
        System.out.println(name);
        List<Integer> program = compiler.compile(loadFile(name), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(108, result);
    }

    @Test
    public void _3x5() {
        String name = "3x5.txt";
        System.out.println(name);
        List<Integer> program = compiler.compile(loadFile(name), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(15, result);
    }

    @Test
    public void _128x2000() {
        String name = "128x2000.txt";
        System.out.println(name);
        List<Integer> program = compiler.compile(loadFile(name), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals((128 * 2000) & 0xffff, result);
    }

    @Test
    public void loop() {
        String name = "loop.txt";
        System.out.println(name);
        List<Integer> program = compiler.compile(loadFile(name), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(0, result);
    }

    @Test
    public void fibonacci() {
        String name = "fibonacci.txt";
        System.out.println(name);
        List<Integer> program = compiler.compile(loadFile(name), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(13, result);
    }

    @Test
    public void relational() {
        String name = "relational.txt";
        System.out.println(name);
        List<Integer> program = compiler.compile(loadFile(name), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(1, result);
    }

    private long compute(List<Integer> program, int width, int stack) {
        ProgramStats stats = new ProgramStats();
        Computer computer = new Computer(width, program, program.size() + stack, instructionSet);
        Engine engine = new Engine();
        while (!isTerminal(Math.toIntExact(computer.instruction.getSignals().getAsLong()), instructionSet)) {
            stats.getCycles().add(computer.advanceCycle(engine));
        }
        System.out.println("Statistics:");
        System.out.println("duration: " + stats.duration());
        System.out.println("cycles: " + stats.getCycles().size());
        System.out.println("ticks: " + stats.ticks());
        System.out.println("gate updates: " + stats.updatedGates());
        System.out.println();
        return computer.ax.getSignals().getAsLong();
    }

    private static String loadFile(String filename) {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            return new String(Files.readAllBytes(new File(classloader.getResource(filename).toURI()).toPath()));
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static boolean isTerminal(int instructionCode, MachineInstructionSet instructionSet) {
        return instructionCode == instructionSet.codeByInstruction(instructionSet.map().terminate());
    }
}
