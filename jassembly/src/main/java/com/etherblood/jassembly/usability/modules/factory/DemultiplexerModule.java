package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.core.BinaryGate;
import com.etherblood.jassembly.usability.Util;
import com.etherblood.jassembly.usability.modules.ReferenceModule;
import static com.etherblood.jassembly.usability.modules.factory.UnaryOperationModule.identity;
import com.etherblood.jassembly.usability.modules.wires.InputReference;
import com.etherblood.jassembly.usability.modules.wires.OutputReference;
import com.etherblood.jassembly.usability.modules.wires.RelayWireReference;
import com.etherblood.jassembly.usability.modules.wires.Wires;
import static com.etherblood.jassembly.usability.modules.wires.Wires.concat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Philipp
 */
public class DemultiplexerModule extends ReferenceModule {

    private final int width, words;

    DemultiplexerModule(List<InputReference> inputs, List<OutputReference> outputs, int width, int words) {
        super(inputs, outputs);
        this.width = width;
        this.words = words;
    }

    public List<InputReference> getInWord() {
        return getAllInputs().subList(0, width);
    }

    public List<InputReference> getInAddress() {
        return getAllInputs().subList(width, getAllInputs().size());
    }

    public List<OutputReference> getOutWord(int index) {
        return getAllOutputs().subList(width * index, width * (index + 1));
    }

    public int getWidth() {
        return width;
    }

    public int getWords() {
        return words;
    }

    public static DemultiplexerModule create(int width, int words, Consumer<BinaryGate> gates) {
        if (words <= 2) {
            return demultiplexer(width, gates);
        }
        int depth = Util.ceilLog(words);
        int aWords = 1 << (depth - 1);
        int bWords = words - aWords;
        DemultiplexerModule demuxA = create(width, aWords, gates);
        DemultiplexerModule demuxB = create(width, bWords, gates);
        DemultiplexerModule demux = demultiplexer(width, gates);

        UnaryOperationModule relay = identity(demuxA.getInAddress().size(), gates);
        Wires.connect(relay.getOut(), demuxA.getInAddress());
        Wires.connect(relay.getOut().subList(0, demuxB.getInAddress().size()), demuxB.getInAddress());

        Wires.connect(demux.getOutWord(0), demuxA.getInWord());
        Wires.connect(demux.getOutWord(1), demuxB.getInWord());
        return new DemultiplexerModule(
                concat(demux.getInWord(), relay.getIn(), demux.getInAddress()),
                concat(demuxA.getAllOutputs(), demuxB.getAllOutputs()),
                width,
                words);
    }

    private static DemultiplexerModule demultiplexer(int width, Consumer<BinaryGate> gates) {
        BinaryOperationModule and = BinaryOperationModule.and(width, gates);
        BinaryOperationModule nand = BinaryOperationModule.nand(width, gates);
        UnaryOperationModule not = UnaryOperationModule.not(width, gates);
        UnaryOperationModule word = UnaryOperationModule.identity(width, gates);
        RelayWireReference address = new RelayWireReference();

        Wires.connect(address, nand.getInA());
        Wires.connect(word.getOut(), and.getInB());
        Wires.connect(word.getOut(), nand.getInB());
        Wires.connect(nand.getOut(), and.getInA());
        Wires.connect(nand.getOut(), not.getIn());

        return new DemultiplexerModule(
                Wires.concat(word.getIn(), Arrays.asList(address)),
                Wires.concat(and.getOut(), not.getOut()),
                width,
                2);
    }
}
