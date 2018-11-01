package com.etherblood.circuit.usability.modules;

import com.etherblood.circuit.core.BinaryGate;
import com.etherblood.circuit.core.Wire;
import com.etherblood.circuit.usability.signals.WireReference;
import java.util.function.IntFunction;

/**
 *
 * @author Philipp
 */
public class ModuleUtil {

    public static WireReference inA(BinaryGate gate) {
        return new WireReference() {
            @Override
            public Wire getWire() {
                return gate.getA();
            }

            @Override
            public void setWire(Wire wire) {
                gate.setA(wire);
            }
        };
    }

    public static WireReference inB(BinaryGate gate) {
        return new WireReference() {
            @Override
            public Wire getWire() {
                return gate.getB();
            }

            @Override
            public void setWire(Wire wire) {
                gate.setB(wire);
            }
        };
    }

    public static WireReference combine(WireReference... consumers) {
        assert consumers.length <= Wire.MAX_CHILDS;
        return new WireReference() {
            @Override
            public Wire getWire() {
                Wire wire = consumers[0].getWire();
                for (WireReference consumer : consumers) {
                    assert consumer.getWire() == wire : "Tried to set wire value before initialization.";
                }
                return wire;
            }

            @Override
            public void setWire(Wire wire) {
                for (WireReference consumer : consumers) {
                    consumer.setWire(wire);
                }
            }
        };
    }

    public static Wire[] concat(Wire[]... arrays) {
        return concat(Wire[]::new, arrays);
    }

    public static BinaryGate[] concat(BinaryGate[]... arrays) {
        return concat(BinaryGate[]::new, arrays);
    }

    public static WireReference[] concat(WireReference[]... arrays) {
        return concat(WireReference[]::new, arrays);
    }

    private static <T> T[] concat(IntFunction<T[]> constructor, T[]... arrays) {
        int length = 0;
        for (T[] array : arrays) {
            length += array.length;
        }
        T[] result = constructor.apply(length);
        int index = 0;
        for (T[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }
        return result;
    }

    @SafeVarargs
    public static final <T> T[] array(T... array) {
        return array;
    }

    @SafeVarargs
    public static final WireReference[] array(WireReference... array) {
        return array;
    }
}
