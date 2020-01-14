package com.etherblood.jassembly.usability.modules.factory;

import com.etherblood.jassembly.core.BinaryGate;
import com.etherblood.jassembly.usability.Util;
import com.etherblood.jassembly.usability.modules.ReferenceModule;
import static com.etherblood.jassembly.usability.modules.factory.BinaryOperationModule.nand;
import static com.etherblood.jassembly.usability.modules.factory.UnaryOperationModule.identity;
import static com.etherblood.jassembly.usability.modules.factory.UnaryOperationModule.not;
import com.etherblood.jassembly.usability.modules.wires.InputReference;
import com.etherblood.jassembly.usability.modules.wires.OutputReference;
import com.etherblood.jassembly.usability.modules.wires.RelayWireReference;
import com.etherblood.jassembly.usability.modules.wires.WireOutputReference;
import com.etherblood.jassembly.usability.modules.wires.Wires;
import static com.etherblood.jassembly.usability.modules.wires.Wires.concat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Philipp
 */
public class MultiplexerModule extends ReferenceModule {

    private final int width, words;

    MultiplexerModule(List<InputReference> inputs, List<OutputReference> outputs, int width, int words) {
        super(inputs, outputs);
        this.width = width;
        this.words = words;
    }

    public List<InputReference> getInWords() {
        return getAllInputs().subList(0, width * words);
    }

    public List<InputReference> getInWord(int index) {
        return getAllInputs().subList(width * index, width * (index + 1));
    }

    public List<InputReference> getInAddress() {
        return getAllInputs().subList(width * words, getAllInputs().size());
    }

    public List<OutputReference> getOutWord() {
        return getAllOutputs();
    }

    public int getWidth() {
        return width;
    }

    public int getWords() {
        return words;
    }

    public static MultiplexerModule create(int width, int words, Consumer<BinaryGate> gates) {
        if (words == 1) {
            UnaryOperationModule identity = identity(width, gates);
            return new MultiplexerModule(
                    concat(identity.getIn(), Arrays.asList(new RelayWireReference())),
                    identity.getOut(),
                    width,
                    words);
        }
        if (words == 2) {
            MultiplexerModule multiplexer = multiplexer(width, gates);
            List<InputReference> unusedWords = multiplexer.getAllInputs().subList(width * words, 2 * width);
            Wires.connect(WireOutputReference.offList(unusedWords.size()), unusedWords);
            return multiplexer;
        }
        int depth = Util.ceilLog(words);
        int aWords = 1 << (depth - 1);
        int bWords = words - aWords;
        MultiplexerModule muxA = create(width, aWords, gates);
        MultiplexerModule muxB = create(width, bWords, gates);
        MultiplexerModule mux = multiplexer(width, gates);

        UnaryOperationModule relay = identity(muxA.getInAddress().size(), gates);
        Wires.connect(relay.getOut(), muxA.getInAddress());
        Wires.connect(relay.getOut().subList(0, muxB.getInAddress().size()), muxB.getInAddress());

        Wires.connect(muxA.getOutWord(), mux.getInWord(0));
        Wires.connect(muxB.getOutWord(), mux.getInWord(1));
        return new MultiplexerModule(
                concat(muxA.getInWords(), muxB.getInWords(), relay.getIn(), mux.getInAddress()),
                mux.getOutWord(),
                width,
                words);
    }

    private static MultiplexerModule multiplexer(int width, Consumer<BinaryGate> gates) {
        BinaryOperationModule nandA = nand(width, gates);
        BinaryOperationModule nandB = nand(width, gates);
        BinaryOperationModule nand = nand(width, gates);
        UnaryOperationModule not = not(width, gates);

        Wires.connect(not.getOut(), nandA.getInB());
        Wires.connect(nandA.getOut(), nand.getInA());
        Wires.connect(nandB.getOut(), nand.getInB());

        RelayWireReference address = new RelayWireReference();
        Wires.connect(address, not.getIn());
        Wires.connect(address, nandB.getInB());

        return new MultiplexerModule(
                concat(nandA.getInA(), nandB.getInA(), Arrays.asList(address)),
                nand.getOut(),
                width,
                2);
    }
}
