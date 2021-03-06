package com.etherblood.jassembly.usability.code;

/**
 *
 * @author Philipp
 */
public class RangeBuilder {

    private int index = 0;

    public Range next(int size) {
        try {
            return new Range(index, size);
        } finally {
            index += size;
        }
    }

    public int getIndex() {
        return index;
    }
}
