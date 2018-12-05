package com.etherblood.jassembly.compile.jassembly.language.ast;

/**
 *
 * @author Philipp
 */
public class Program {

    private final FunctionDeclaration[] functions;

    public Program(FunctionDeclaration... functions) {
        this.functions = functions;
    }

    public FunctionDeclaration[] getFunctions() {
        return functions;
    }
    
    public FunctionDeclaration getFunction(String identifier) {
        for (FunctionDeclaration function : functions) {
            if(function.getIdentifier().equals(identifier)) {
                return function;
            }
        }
        throw new NullPointerException("Function " + identifier + " does not exist.");
    }
}
