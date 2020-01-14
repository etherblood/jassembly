package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.usability.Util;
import com.etherblood.jassembly.usability.modules.wires.WireOutputReference;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Philipp
 */
public class MultiplexerModuleTest extends ReferenceModuleTestBase {

    @Test
    public void testCreate() {
        int width = 5;
        int words = 6;
        int depth = Util.ceilLog(words);
        MultiplexerModule mux = MultiplexerModule.create(width, words, engine::activate);
        assertEquals(width * words + depth, mux.getAllInputs().size());
        assertEquals(width, mux.getAllOutputs().size());

        List<List<WireOutputReference>> wordList = new ArrayList<>();
        for (int i = 0; i < words; i++) {
            wordList.add(variable(mux.getInWord(i)));
        }
        List<WireOutputReference> address = variable(mux.getInAddress());

        for (int i = 0; i < words; i++) {
            List<WireOutputReference> word = wordList.get(i);
            setValue(word, i);
        }
        for (int i = 0; i < words; i++) {
            setValue(address, i);
            compute();
            assertEquals(i, getValue(wordList.get(i)));
        }
    }
}
