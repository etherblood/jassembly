package com.etherblood.jassembly.usability.modules.wires;

import com.etherblood.jassembly.core.BinaryGate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Philipp
 */
public class Wires {

    public static void connect(OutputReference source, InputReference consumer) {
        source.connectTo(consumer);
        consumer.connectTo(source);
    }

    public static void connect(OutputReference source, List<? extends InputReference> consumer) {
        for (int i = 0; i < consumer.size(); i++) {
            connect(source, consumer.get(i));
        }
    }

    public static void connect(List<? extends OutputReference> source, List<? extends InputReference> consumer) {
        if (source.size() != consumer.size()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < source.size(); i++) {
            connect(source.get(i), consumer.get(i));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> concat(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    public static void setValue(List<? extends OutputReference> word, long value, Consumer<BinaryGate> activator) {
        for (OutputReference wire : word) {
            wire.getWire().setSignal((value & 1) != 0);
            value >>>= 1;
            for (BinaryGate child : wire.getWire().childs()) {
                activator.accept(child);
            }
        }
    }

    public static long getValue(List<? extends OutputReference> word) {
        long flag = 1;
        long result = 0;
        for (OutputReference wire : word) {
            if (wire.getWire().getSignal()) {
                result |= flag;
            }
            flag <<= 1;
        }
        return result;
    }

    public static InputReference inA(BinaryGate gate) {
        return new GateInputAReference(gate);
    }

    public static InputReference inB(BinaryGate gate) {
        return new GateInputBReference(gate);
    }

    public static OutputReference out(BinaryGate gate) {
        return new GateOutputReference(gate);
    }
}
