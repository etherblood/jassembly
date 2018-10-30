package com.etherblood.circuit.usability;

import com.etherblood.circuit.core.Wire;
import com.etherblood.circuit.usability.modules.ModuleUtil;
import com.etherblood.circuit.usability.modules.SimpleModule;
import com.etherblood.circuit.usability.signals.WireReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class BusUtil {

    public static long getInput(SimpleModule mod, int offset, int width) {
        WireReference[] inputs = mod.getInputs();
        long result = 0;
        for (int i = 0; i < width; i++) {
            if (inputs[offset + i].getWire().getSignal()) {
                result |= 1L << i;
            }
        }
        return result;
    }

    public static void setInput(SimpleModule mod, int offset, int width, long value) {
        WireReference[] inputs = mod.getInputs();
        for (int i = 0; i < width; i++) {
            inputs[offset].setWire(((value & 1) != 0) ? Wire.on() : Wire.off());
            offset++;
            value >>>= 1;
        }
    }

    public static long getOutput(SimpleModule mod, int offset, int width) {
        Wire[] outputs = mod.getOutputs();
        long result = 0;
        for (int i = 0; i < width; i++) {
            if (outputs[offset + i].getSignal()) {
                result |= 1L << i;
            }
        }
        return result;
    }

    public static void connect(SimpleModule source, int sourceIndex, SimpleModule destination, int destinationIndex, int width) {
        connect(outBus(source, sourceIndex, width), inBus(destination, destinationIndex, width));
    }

    public static List<Wire> outBus(SimpleModule mod, int index, int width) {
        return Arrays.asList(mod.getOutputs()).subList(index, index + width);
    }

    public static List<WireReference> inBus(SimpleModule mod, int index, int width) {
        return Arrays.asList(mod.getInputs()).subList(index, index + width);
    }

    public static void connect(List<Wire> source, List<WireReference> destination) {
        assert source.size() == destination.size();
        for (int i = 0; i < source.size(); i++) {
            destination.get(i).setWire(source.get(i));
        }
    }
    
    public static List<WireReference> combineBuses(List<WireReference>... ref) {
        List<WireReference> result = new ArrayList<>();
        int len = ref[0].size();
        for (int i = 0; i < len; i++) {
            List<WireReference> items = new ArrayList<>();
            for (List<WireReference> list : ref) {
                items.add(list.get(i));
            }
            result.add(ModuleUtil.combine(items.toArray(new WireReference[items.size()])));
        }
        return result;
    }
}
