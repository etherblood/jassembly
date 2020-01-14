package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.usability.Util;
import com.etherblood.jassembly.usability.modules.wires.WireOutputReference;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Philipp
 */
public class DemultiplexerModuleTest extends ReferenceModuleTestBase {

    @Test
    public void recursiveDemultiplexer() {
        int width = 5;
        int words = 6;
        int depth = Util.ceilLog(words);
        DemultiplexerModule demux = DemultiplexerModule.create(width, words, engine::activate);
        assertEquals(width + depth, demux.getAllInputs().size());

        List<WireOutputReference> word = variable(demux.getInWord());
        List<WireOutputReference> address = variable(demux.getInAddress());

        int input = 23;
        setValue(word, input);
        for (int selected = 0; selected < words; selected++) {
            setValue(address, selected);
            compute();
            for (int i = 0; i < words; i++) {
                assertEquals(selected == i ? input : 0, getValue(demux.getOutWord(i)));
            }
        }
    }

}
