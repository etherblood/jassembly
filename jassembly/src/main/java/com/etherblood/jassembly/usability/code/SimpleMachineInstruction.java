package com.etherblood.jassembly.usability.code;

public class SimpleMachineInstruction implements MachineInstruction {

    private final long controlFlags;
    private final String humanReadable;

    public SimpleMachineInstruction(String humanReadable, long controlFlags) {
        this.humanReadable = humanReadable;
        this.controlFlags = controlFlags;
    }

    @Override
    public long getControlFlags() {
        return controlFlags;
    }

    @Override
    public int hashCode() {
        return 67 + Long.hashCode(controlFlags);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SimpleMachineInstruction)) {
            return false;
        }
        SimpleMachineInstruction other = (SimpleMachineInstruction) obj;
        return this.controlFlags == other.getControlFlags();
    }

    @Override
    public String toString() {
        return humanReadable + " [0x" + Long.toHexString(controlFlags) + "]";
}

}
