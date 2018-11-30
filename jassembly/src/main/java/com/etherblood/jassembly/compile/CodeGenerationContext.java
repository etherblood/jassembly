package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.compile.ast.FunctionDeclaration;

/**
 *
 * @author Philipp
 */
public class CodeGenerationContext {

    private final VariablesContext vars;
    private final String loopStart, loopEnd;
    private final FunctionDeclaration function;

    public CodeGenerationContext() {
        this(new VariablesContext(), null, null, null);
    }

    private CodeGenerationContext(VariablesContext vars, String loopStart, String loopEnd, FunctionDeclaration function) {
        this.vars = vars;
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
        this.function = function;
    }

    public CodeGenerationContext withLoopLabels(String start, String end) {
        return new CodeGenerationContext(vars, start, end, function);
    }

    public CodeGenerationContext withNewScope() {
        return new CodeGenerationContext(vars.childContext(), loopStart, loopEnd, function);
    }

    public CodeGenerationContext withFunctionScope(FunctionDeclaration function) {
        return new CodeGenerationContext(new VariablesContext(), loopStart, loopEnd, function);
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

    public FunctionDeclaration getFunction() {
        return function;
    }
}
