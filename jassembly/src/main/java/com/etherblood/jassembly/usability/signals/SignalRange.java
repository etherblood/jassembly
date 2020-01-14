package com.etherblood.jassembly.usability.signals;

import java.util.BitSet;

/**
 *
 * @author Philipp
 */
public class SignalRange {

    private final int offset, length;
    private final HasSignal[] signals;

    public SignalRange(HasSignal... signals) {
        this(0, signals.length, signals);
    }

    public SignalRange(int offset, int length, HasSignal... signals) {
        this.offset = offset;
        this.length = length;
        this.signals = signals;
    }

    public int size() {
        return length;
    }

    public HasSignal get(int index) {
        return signals[offset + index];
    }

    public BitSet get() {
        BitSet result = new BitSet(length);
        for (int i = 0; i < length; i++) {
            result.set(i, get(i).getSignal());
        }
        return result;
    }

    public long getAsLong() {
        long result = 0;
        for (int i = 0; i < length; i++) {
            if (get(i).getSignal()) {
                result |= 1L << i;
            }
        }
        return result;
    }

    public void set(BitSet signal) {
        for (int i = 0; i < length; i++) {
            get(i).setSignal(signal.get(i));
        }
    }

    public void set(long signal) {
        for (int i = 0; i < length; i++) {
            get(i).setSignal(((signal >> i) & 1) != 0);
        }
    }

    public SignalRange subRange(int offset, int length) {
        return new SignalRange(this.offset + offset, length, signals);
    }

    public String toHexStrig() {
        BitSet bits = get();
        byte[] a = bits.toByteArray();
        byte[] b = new byte[-Math.floorDiv(size(), -8)];
        for (int i = 0; i < b.length; i++) {
            int j = b.length - i - 1;
            if (j < a.length) {
                b[i] = a[j];
            } else {
                b[i] = 0;
            }
        }
        String result = "";
        for (int i = b.length - 1; i >= 0; i--) {
            result += Integer.toHexString(b[i]);
        }
        return "0x" + result;

    }

    public static SignalRange concat(SignalRange... ranges) {
        int size = 0;
        for (SignalRange range : ranges) {
            size += range.size();
        }
        HasSignal[] signals = new HasSignal[size];
        int index = 0;
        for (SignalRange range : ranges) {
            System.arraycopy(range.signals, range.offset, signals, index, range.size());
            index += range.size();
        }
        return new SignalRange(signals);
    }

}
