package com.etherblood.jassembly.compile.jassembly;

import com.etherblood.jassembly.compile.jassembly.JassemblyContext;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public abstract class Labelled {

    private final List<String> labels = new ArrayList<>();

    abstract int toCode(JassemblyContext context);

    public List<String> getLabels() {
        return labels;
    }
}
