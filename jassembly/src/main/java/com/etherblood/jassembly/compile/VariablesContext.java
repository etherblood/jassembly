package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.compile.ast.VariableDetails;
import com.etherblood.jassembly.compile.ast.expression.ExpressionType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Philipp
 */
public class VariablesContext {

    private final Map<String, VariableDeclaration> variables;
    private int parameterOffset, variableOffset;

    public VariablesContext() {
        this(new HashMap<>(), 3, 0);
    }

    private VariablesContext(Map<String, VariableDeclaration> variableOffsets, int parameterOffset, int variableOffset) {
        this.variables = variableOffsets;
        this.parameterOffset = parameterOffset;
        this.variableOffset = variableOffset;
    }

    public void declareParameter(VariableDetails variable) {
        declareParameter(variable.getName(), variable.getType());
    }

    public void declareParameter(String name, ExpressionType type) {
        if (variables.put(name, new VariableDeclaration(name, type, parameterOffset)) != null) {
            throw new IllegalStateException("parameter '" + name + "' declared twice.");
        }
        parameterOffset++;
    }

    public void declareVariable(VariableDetails variable) {
        declareVariable(variable.getName(), variable.getType());
    }

    public void declareVariable(String name, ExpressionType type) {
        if (variables.put(name, new VariableDeclaration(name, type, variableOffset)) != null) {
            throw new IllegalStateException("variable '" + name + "' declared twice.");
        }
        variableOffset--;
    }

    public VariableDeclaration getDetails(String name) {
        VariableDeclaration var = variables.get(name);
        if (var == null) {
            throw new IllegalStateException("tried to access undeclared variable '" + name + "'");
        }
        return var;
    }

    public int varCount() {
        return -variableOffset;
    }

    public VariablesContext childContext() {
        return new VariablesContext(new HashMap<>(variables), parameterOffset, variableOffset);
    }
}
