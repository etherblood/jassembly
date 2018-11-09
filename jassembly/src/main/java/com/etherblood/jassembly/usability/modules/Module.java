package com.etherblood.jassembly.usability.modules;

import com.etherblood.jassembly.core.Wire;

/**
 *
 * @author Philipp
 */
public interface Module {

    Wire[] inputs();
    Wire[] outputs();
}
