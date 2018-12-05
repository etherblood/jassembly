package com.etherblood.jassembly.compile.jassembly.language;

import com.etherblood.jassembly.compile.jassembly.language.ast.VariableDetails;
import com.etherblood.jassembly.compile.jassembly.language.ast.expression.ExpressionType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Philipp
 */
public class VariablesContext {

    private final Map<String, VariableMeta> variables;
    private int parameterOffset, variableOffset;

    public VariablesContext() {
        this(new HashMap<>(), 3, 0);
    }

    private VariablesContext(Map<String, VariableMeta> variableOffsets, int parameterOffset, int variableOffset) {
        this.variables = variableOffsets;
        this.parameterOffset = parameterOffset;
        this.variableOffset = variableOffset;
    }

    public void declareParameter(VariableDetails variable) {
        declareParameter(variable.getName(), variable.getType());
    }

    public void declareParameter(String name, ExpressionType type) {
        if (variables.put(name, new VariableMeta(name, type, parameterOffset)) != null) {
            throw new IllegalStateException("parameter '" + name + "' declared twice.");
        }
        parameterOffset++;
    }

    public void declareVariable(VariableDetails variable) {
        declareVariable(variable.getName(), variable.getType());
    }

    public void declareVariable(String name, ExpressionType type) {
        if (variables.put(name, new VariableMeta(name, type, variableOffset)) != null) {
            throw new IllegalStateException("variable '" + name + "' declared twice.");
        }
        variableOffset--;
    }

    public VariableMeta getDetails(String name) {
        VariableMeta var = variables.get(name);
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
