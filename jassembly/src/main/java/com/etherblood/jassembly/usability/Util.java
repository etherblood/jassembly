package com.etherblood.jassembly.usability;

/**
 *
 * @author Philipp
 */
public class Util {

    public static int ceilDiv(int nom, int denom) {
        return -Math.floorDiv(-nom, denom);
    }

    public static int floorLog(int bits) {
        if (bits == 0) {
            throw new IllegalArgumentException();
        }
        return 31 - Integer.numberOfLeadingZeros(bits);
    }

    public static int ceilLog(int bits) {
        if (Integer.bitCount(bits) <= 1) {
            return floorLog(bits);
        }
        return floorLog(bits) + 1;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] array(T... array) {
        return array;
    }

}
