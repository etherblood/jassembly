package com.etherblood.jassembly.usability.signals;

import com.etherblood.jassembly.core.Wire;

/**
 *
 * @author Philipp
 */
public interface WireReference {

    Wire getWire();

    void setWire(Wire wire);
}
