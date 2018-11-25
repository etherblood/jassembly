package com.etherblood.jassembly.usability.monitoring;

import com.etherblood.jassembly.usability.code.MachineInstruction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class CycleStats {

    private final MachineInstruction instruction;
    private final List<TickStats> ticks = new ArrayList<>();

    public CycleStats(MachineInstruction instruction) {
        this.instruction = instruction;
    }

    public MachineInstruction getInstruction() {
        return instruction;
    }

    public List<TickStats> getTicks() {
        return ticks;
    }
    
    public long nanoDuration() {
        return ticks.stream().mapToLong(TickStats::getNanoDuration).sum();
    }

}
