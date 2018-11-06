package com.etherblood.circuit.computer;

import com.etherblood.circuit.compile.SimpleCompiler;
import com.etherblood.circuit.core.Engine;
import com.etherblood.circuit.usability.signals.SignalRange;
import com.etherblood.circuit.usability.codes.Command;
import com.etherblood.circuit.usability.codes.programs.If;
import com.etherblood.circuit.usability.codes.programs.Blocks;
import com.etherblood.circuit.usability.codes.programs.Commands;
import com.etherblood.circuit.usability.codes.programs.SimpleCommandConsumer;
import com.etherblood.circuit.usability.codes.programs.While;
import java.util.ArrayList;
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
        int a = 13;
        int b = 6;
        {
            int x1 = a;
            int x2 = b;
            int x3 = 0;
            while (x2 != 0) {
                if ((x2 & 1) != 0) {
                    x3 += x1;
                }
                x2 >>>= 1;
                x1 <<= 1;
            }
            int acc = x3;
        }
        //program above is encoded in methods below
        List<Integer> program_0 = multiplyProgram_0(a, b);
        List<Integer> program_1 = multiplyProgram_1(a, b);
        
        
        String sampleCode = "int main() {\n"
                + "    return -~2;\n"
                + "}";
        List<Integer> program_2 = new SimpleCompiler().compile(sampleCode);

        int width = 16;
        Computer computer = new Computer(width, program_2, 100);
        Engine engine = new Engine();
        while (computer.command.getSignals().getAsLong() != Command.TERMINATE.ordinal()) {
            advanceCycle(computer, engine);
            printState(computer);
        }
    }

    private static void advanceCycle(Computer computer, Engine engine) {
        computer.clockWire.setSignal(!computer.clockWire.getSignal());
        engine.activate(computer.clockWire);
        while (engine.isActive()) {
            engine.tick();
        }
        computer.clockWire.setSignal(!computer.clockWire.getSignal());
        engine.activate(computer.clockWire);
        while (engine.isActive()) {
            engine.tick();
        }
    }

    private static void printState(Computer computer) {
        SignalRange currentCommand = computer.command.getSignals();
        System.out.println("ram: " + computer.ram.getSignals().toHexStrig());
        System.out.println("pc: " + computer.pc.getSignals().toHexStrig() + " (" + computer.pc.getSignals().getAsLong() + ")");
        System.out.println("cmd: " + currentCommand.toHexStrig() + " (" + Command.values()[(int) currentCommand.getAsLong()] + ")");
        System.out.println("ac: " + computer.acc.getSignals().toHexStrig() + " (" + computer.acc.getSignals().getAsLong() + ")");
        System.out.println("x0: " + computer.x0.getSignals().toHexStrig() + " (" + computer.x0.getSignals().getAsLong() + ")");
        System.out.println("x1: " + computer.x1.getSignals().toHexStrig() + " (" + computer.x1.getSignals().getAsLong() + ")");
        System.out.println("x2: " + computer.x2.getSignals().toHexStrig() + " (" + computer.x2.getSignals().getAsLong() + ")");
        System.out.println("x3: " + computer.x3.getSignals().toHexStrig() + " (" + computer.x3.getSignals().getAsLong() + ")");
        System.out.println();
    }

    private static List<Integer> multiplyProgram_0(int a, int b) {
        int loopHead = 6, loopBody = 17, loopEnd = 49;
        int ifBody = 32, ifEnd = 37;
        List<Integer> program = new ArrayList<>();
        program.add(Command.LOAD_CONST.ordinal());
        program.add(a);
        program.add(Command.TO_X1.ordinal());
        program.add(Command.LOAD_CONST.ordinal());
        program.add(b);
        program.add(Command.TO_X2.ordinal());
        // loop_head:
        program.add(Command.FROM_X2.ordinal());
        program.add(Command.ANY.ordinal());
        program.add(Command.TO_X0.ordinal());
        program.add(Command.LOAD_CONST.ordinal());
        program.add(loopBody ^ loopEnd);
        program.add(Command.AND.ordinal());
        program.add(Command.TO_X0.ordinal());
        program.add(Command.LOAD_CONST.ordinal());
        program.add(loopEnd);
        program.add(Command.XOR.ordinal());
        program.add(Command.JUMP.ordinal());
        // loop_body:
        // if_head:
        program.add(Command.FROM_X2.ordinal());
        program.add(Command.TO_X0.ordinal());
        program.add(Command.LOAD_CONST.ordinal());
        program.add(1);
        program.add(Command.AND.ordinal());
        program.add(Command.ANY.ordinal());
        program.add(Command.TO_X0.ordinal());
        program.add(Command.LOAD_CONST.ordinal());
        program.add(ifBody ^ ifEnd);
        program.add(Command.AND.ordinal());
        program.add(Command.TO_X0.ordinal());
        program.add(Command.LOAD_CONST.ordinal());
        program.add(ifEnd);
        program.add(Command.XOR.ordinal());
        program.add(Command.JUMP.ordinal());
        // if_body:
        program.add(Command.FROM_X1.ordinal());
        program.add(Command.TO_X0.ordinal());
        program.add(Command.FROM_X3.ordinal());
        program.add(Command.ADD.ordinal());
        program.add(Command.TO_X3.ordinal());
        // if_end:
        program.add(Command.LOAD_CONST.ordinal());
        program.add(1);
        program.add(Command.TO_X0.ordinal());
        program.add(Command.FROM_X2.ordinal());
        program.add(Command.RSHIFT.ordinal());
        program.add(Command.TO_X2.ordinal());
        program.add(Command.FROM_X1.ordinal());
        program.add(Command.LSHIFT.ordinal());
        program.add(Command.TO_X1.ordinal());
        program.add(Command.LOAD_CONST.ordinal());
        program.add(loopHead);
        program.add(Command.JUMP.ordinal());
        // loop_end:
        program.add(Command.FROM_X3.ordinal());
        program.add(Command.TERMINATE.ordinal());
        return program;
    }

    private static List<Integer> multiplyProgram_1(int a, int b) {
        SimpleCommandConsumer consume = new SimpleCommandConsumer();
        new Blocks(
                new Commands(
                        Command.LOAD_CONST.ordinal(),
                        a,
                        Command.TO_X1.ordinal(),
                        Command.LOAD_CONST.ordinal(),
                        b,
                        Command.TO_X2.ordinal()
                ),
                new While(
                        new Commands(Command.FROM_X2.ordinal()),
                        new Blocks(
                                new If(
                                        new Commands(
                                                Command.FROM_X2.ordinal(),
                                                Command.TO_X0.ordinal(),
                                                Command.LOAD_CONST.ordinal(),
                                                1,
                                                Command.AND.ordinal()
                                        ),
                                        new Commands(
                                                Command.FROM_X1.ordinal(),
                                                Command.TO_X0.ordinal(),
                                                Command.FROM_X3.ordinal(),
                                                Command.ADD.ordinal(),
                                                Command.TO_X3.ordinal()
                                        )),
                                new Commands(
                                        Command.LOAD_CONST.ordinal(),
                                        1,
                                        Command.TO_X0.ordinal(),
                                        Command.FROM_X2.ordinal(),
                                        Command.RSHIFT.ordinal(),
                                        Command.TO_X2.ordinal(),
                                        Command.FROM_X1.ordinal(),
                                        Command.LSHIFT.ordinal(),
                                        Command.TO_X1.ordinal()
                                )
                        )
                ),
                new Commands(
                        Command.FROM_X3.ordinal(),
                        Command.TERMINATE.ordinal()
                )
        ).toCommands(consume);
        return consume.getList();
    }

}
