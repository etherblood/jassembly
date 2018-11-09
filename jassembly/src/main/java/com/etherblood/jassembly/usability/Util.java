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
            return 0; // or throw exception
        }
        return 31 - Integer.numberOfLeadingZeros(bits);
    }

    public static int ceilLog(int bits) {
        if(Integer.bitCount(bits) <= 1) {
            return floorLog(bits);
        }
        return floorLog(bits) + 1;
    }

}
