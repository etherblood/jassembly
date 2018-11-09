package com.etherblood.jassembly.compile.jassembly;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public abstract class JassemblyCommand {

    private final List<String> labels = new ArrayList<>();

    abstract int toCode(List<JassemblyCommand> contextCommands);

    public List<String> getLabels() {
        return labels;
    }
}
