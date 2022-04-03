package de.zebrajaeger.phserver.util;

public class NumberConverter {
    public static int int32toInt24(int i) {
        if (i >= 0) {
            return i & 0x00ffffff;
        } else {
            return (i & 0x00ffffff) | 0x00800000;
        }
    }

    public static int unsigned24ToSigned(int i) {
        // 0x00ffffff = -1
        // 0x00800000 = -2147483648
        // 0x007fffff = 2147483647
        // 0x00000000 = 0

        return ((i & 0x00800000) == 0) ? i : i - 0x01000000;
    }

    public static int unsigned16ToSigned(int i) {
        // 0x0000ffff = -1
        // 0x00008000 = -2147483648/256
        // 0x00007fff = 2147483647/256
        // 0x00000000 = 0

        return ((i & 0x00008000) == 0) ? i : i - 0x00010000;
    }

}
