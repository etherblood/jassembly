package com.etherblood.jassembly.usability.monitoring;

/**
 *
 * @author Philipp
 */
public class TickStats {

    private final int nanoDuration;
    private final int updatedGatesCount;

    public TickStats(int nanoDuration, int updatedGatesCount) {
        this.nanoDuration = nanoDuration;
        this.updatedGatesCount = updatedGatesCount;
    }

    public int getNanoDuration() {
        return nanoDuration;
    }

    public int getUpdatedGatesCount() {
        return updatedGatesCount;
    }
}
