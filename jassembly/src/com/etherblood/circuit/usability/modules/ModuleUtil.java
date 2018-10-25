package com.etherblood.circuit.usability.modules;

import com.etherblood.circuit.core.BinaryGate;
import com.etherblood.circuit.core.Wire;
import com.etherblood.circuit.usability.signals.WireReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                    assert consumer.getWire() == wire: "Tried to set wire value before initialization.";
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

    public static Wire[] concat(Wire[]... a) {
        List<Wire> list = new ArrayList<>();
        for (Wire[] wires : a) {
            list.addAll(Arrays.asList(wires));
        }
        return list.toArray(new Wire[list.size()]);
    }

    public static BinaryGate[] concat(BinaryGate[]... a) {
        List<BinaryGate> list = new ArrayList<>();
        for (BinaryGate[] gates : a) {
            list.addAll(Arrays.asList(gates));
        }
        return list.toArray(new BinaryGate[list.size()]);
    }

    public static WireReference[] concat(WireReference[]... a) {
        List<WireReference> list = new ArrayList<>();
        for (WireReference[] consumers : a) {
            list.addAll(Arrays.asList(consumers));
        }
        return list.toArray(new WireReference[list.size()]);
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
