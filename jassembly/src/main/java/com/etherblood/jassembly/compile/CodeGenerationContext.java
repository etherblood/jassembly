package com.etherblood.jassembly.compile;

/**
 *
 * @author Philipp
 */
public class CodeGenerationContext {

    private final VariablesContext vars;
    private final String loopStart, loopEnd;

    public CodeGenerationContext() {
        this(new VariablesContext(), null, null);
    }

    private CodeGenerationContext(VariablesContext vars, String loopStart, String loopEnd) {
        this.vars = vars;
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
    }

    public CodeGenerationContext withLoopLabels(String start, String end) {
        return new CodeGenerationContext(vars, start, end);
    }

    public CodeGenerationContext withNewScope() {
        return new CodeGenerationContext(vars.childContext(), loopStart, loopEnd);
    }

    public CodeGenerationContext clearVars() {
        return new CodeGenerationContext(new VariablesContext(), loopStart, loopEnd);
    }

    public VariablesContext getVars() {
        return vars;
    }

    public String getLoopStart() {
        return loopStart;
    }

    public String getLoopEnd() {
        return loopEnd;
    }
}
