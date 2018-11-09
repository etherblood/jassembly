package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.jassembly.Jassembly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Philipp
 */
public class CodeGenerationContext {

    private final Jassembly jassembly;
    private final List<String> vars;
    private final String loopStart, loopEnd;

    public CodeGenerationContext(Jassembly jassembly) {
        this(Objects.requireNonNull(jassembly), new ArrayList<>(), null, null);
    }

    private CodeGenerationContext(Jassembly jassembly, List<String> vars, String loopStart, String loopEnd) {
        this.jassembly = jassembly;
        this.vars = vars;
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
    }

    public CodeGenerationContext withLoopLabels(String start, String end) {
        return new CodeGenerationContext(jassembly, vars, start, end);
    }

    public CodeGenerationContext withNewScope() {
        return new CodeGenerationContext(jassembly, new ArrayList<>(vars), loopStart, loopEnd);
    }

    public Jassembly getJassembly() {
        return jassembly;
    }

    public List<String> getVars() {
        return vars;
    }

    public String getLoopStart() {
        return loopStart;
    }

    public String getLoopEnd() {
        return loopEnd;
    }
}
