package com.etherblood.jassembly.usability.code;

/**
 *
 * @author Philipp
 */
public class Range {

    public final int offset, length;

    public Range(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public long getMask() {
        return ((1 << length) - 1) << offset;
    }
}
