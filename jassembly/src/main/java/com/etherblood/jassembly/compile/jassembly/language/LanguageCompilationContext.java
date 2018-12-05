package com.etherblood.jassembly.compile.jassembly.language;

import com.etherblood.jassembly.compile.jassembly.language.ast.FunctionDeclaration;

/**
 *
 * @author Philipp
 */
public class LanguageCompilationContext {

    private final VariablesContext vars;
    private final String loopStart, loopEnd;
    private final FunctionDeclaration function;

    public LanguageCompilationContext() {
        this(new VariablesContext(), null, null, null);
    }

    private LanguageCompilationContext(VariablesContext vars, String loopStart, String loopEnd, FunctionDeclaration function) {
        this.vars = vars;
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
        this.function = function;
    }

    public LanguageCompilationContext withLoopLabels(String start, String end) {
        return new LanguageCompilationContext(vars, start, end, function);
    }

    public LanguageCompilationContext withNewScope() {
        return new LanguageCompilationContext(vars.childContext(), loopStart, loopEnd, function);
    }

    public LanguageCompilationContext withFunctionScope(FunctionDeclaration function) {
        return new LanguageCompilationContext(new VariablesContext(), loopStart, loopEnd, function);
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
