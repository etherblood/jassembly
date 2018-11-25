package com.etherblood.jassembly.usability.monitoring;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class ProgramStats {

    private final List<CycleStats> cycles = new ArrayList<>();

    public List<CycleStats> getCycles() {
        return cycles;
    }

    public long ticks() {
        return cycles.stream().map(CycleStats::getTicks).mapToLong(List::size).sum();
    }

    public long updatedGates() {
        return cycles.stream().map(CycleStats::getTicks).flatMap(List::stream).mapToLong(TickStats::getUpdatedGatesCount).sum();
    }

    public Duration duration() {
        return Duration.ofNanos(cycles.stream().mapToLong(CycleStats::nanoDuration).sum());
    }
}
