package com.etherblood.jassembly.compile;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Philipp
 */
public class VariablesContext {

    private final Map<String, Integer> variableOffsets;
    private int parameterOffset, variableOffset;

    public VariablesContext() {
        this(new HashMap<>(), 2, -1);
    }

    private VariablesContext(Map<String, Integer> variableOffsets, int parameterOffset, int variableOffset) {
        this.variableOffsets = variableOffsets;
        this.parameterOffset = parameterOffset;
        this.variableOffset = variableOffset;
    }

    public void declareParameter(String name) {
        if (variableOffsets.put(name, parameterOffset) != null) {
            throw new IllegalStateException("parameter '" + name + "' declared twice.");
        }
        parameterOffset++;
    }

    public void declareVariable(String name) {
        if (variableOffsets.put(name, variableOffset) != null) {
            throw new IllegalStateException("variable '" + name + "' declared twice.");
        }
        variableOffset--;
    }

    public int getOffset(String name) {
        Integer offset = variableOffsets.get(name);
        if (offset == null) {
            throw new IllegalStateException("tried to access undeclared variable '" + name + "'");
        }
        return offset;
    }

    public int varCount() {
        return -1 - variableOffset;
    }

    public VariablesContext childContext() {
        return new VariablesContext(new HashMap<>(variableOffsets), parameterOffset, variableOffset);
    }
}
