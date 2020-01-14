package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.usability.modules.wires.WireOutputReference;
import java.util.List;
import java.util.function.IntBinaryOperator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Philipp
 */
public class BinaryOperationModuleTest extends ReferenceModuleTestBase {

    @Test
    public void testNand() {
        int width = 5;
        BinaryOperationModule module = BinaryOperationModule.nand(width, engine::activate);
        assertEquals(2 * width, module.getAllInputs().size());
        assertEquals(width, module.getAllOutputs().size());

        List<WireOutputReference> a = variable(module.getInA());
        List<WireOutputReference> b = variable(module.getInB());

        int mask = (1 << width) - 1;
        IntBinaryOperator nand = (x, y) -> mask & ~(x & y);

        setValue(a, 31);
        setValue(b, 13);
        compute();
        assertEquals(nand.applyAsInt(31, 13), getValue(module.getOut()));

        setValue(a, 0);
        setValue(b, 13);
        compute();
        assertEquals(nand.applyAsInt(0, 13), getValue(module.getOut()));

        setValue(a, 5);
        setValue(b, 7);
        compute();
        assertEquals(nand.applyAsInt(5, 7), getValue(module.getOut()));

        setValue(a, 8);
        setValue(b, 20);
        compute();
        assertEquals(nand.applyAsInt(8, 20), getValue(module.getOut()));
    }

}
