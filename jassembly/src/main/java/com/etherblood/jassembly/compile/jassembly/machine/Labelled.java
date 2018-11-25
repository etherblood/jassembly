package com.etherblood.jassembly.compile.jassembly.machine;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public abstract class Labelled {

    private final List<String> labels = new ArrayList<>();

    public List<String> getLabels() {
        return labels;
    }
}
