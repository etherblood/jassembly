package com.etherblood.jassembly.usability.modules.wires;

import com.etherblood.jassembly.core.Wire;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WireOutputReference implements OutputReference {

    private final Wire wire;
    private final List<InputReference> consumers = new ArrayList<>();

    public WireOutputReference(Wire wire) {
        this.wire = wire;
    }

    @Override
    public void connectTo(InputReference consumer) {
        consumers.add(Objects.requireNonNull(consumer));
    }

    @Override
    public Wire getWire() {
        return wire;
    }

    @Override
    public boolean isResolved() {
        return true;
    }

    public static List<WireOutputReference> offList(int size) {
        WireOutputReference[] arr = new WireOutputReference[size];
        Arrays.fill(arr, new WireOutputReference(Wire.off()));
        return Arrays.asList(arr);
    }

}
