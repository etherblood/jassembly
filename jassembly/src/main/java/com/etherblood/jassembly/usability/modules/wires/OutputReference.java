package com.etherblood.jassembly.usability.modules.wires;

import com.etherblood.jassembly.core.Wire;

/**
 *
 * @author Philipp
 */
public interface OutputReference {

    void connectTo(InputReference consumer);

    Wire getWire();
    
    boolean isResolved();
}
