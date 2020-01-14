package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.usability.modules.wires.WireOutputReference;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Philipp
 */
public class FlipFlopModuleTest extends ReferenceModuleTestBase {


    @Test
    public void testRegister() {
        int width = 32;
        FlipFlopModule register = FlipFlopModule.register(width, engine::activate);
        List<WireOutputReference> word = variable(register.getInWord());
        List<WireOutputReference> write = variable(register.getInWrite());
        
        compute();
        assertEquals(0, getValue(register.getOutWord()));
        
        setValue(word, 17);
        compute();
        assertEquals(0, getValue(register.getOutWord()));
        setValue(write, 1);
        compute();
        setValue(write, 0);
        compute();
        assertEquals(17, getValue(register.getOutWord()));
        setValue(word, 38);
        compute();
        assertEquals(17, getValue(register.getOutWord()));
        setValue(write, 1);
        compute();
        setValue(write, 0);
        compute();
        assertEquals(38, getValue(register.getOutWord()));
    }

}