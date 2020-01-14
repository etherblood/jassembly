package com.etherblood.jassembly.usability.modules.wires;

import com.etherblood.jassembly.core.Wire;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class RelayWireReference implements InputReference, OutputReference {

    private OutputReference source;
    private final List<InputReference> consumers = new ArrayList<>();

    @Override
    public void connectTo(OutputReference source) {
        if (this.source != null) {
            throw new IllegalStateException();
        }
        this.source = Objects.requireNonNull(source);
        if(this.source.isResolved()) {
            cascade();
        }
    }

    @Override
    public void connectTo(InputReference consumer) {
        consumers.add(Objects.requireNonNull(consumer));
    }

    @Override
    public Wire getWire() {
        assert isResolved();
        return source.getWire();
    }

    @Override
    public void cascade() {
        assert isResolved();
        for (InputReference consumer : consumers) {
            consumer.cascade();
        }
    }

    @Override
    public boolean isResolved() {
        return source != null && source.isResolved();
    }

}
