package de.zebrajaeger.phserver.hardware;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

public class I2cUtils {
    public static void scan(I2CBus bus) {
        System.out.println(String.format("Scan Bus: %d", bus.getBusNumber()));
        System.out.println("     0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f");
        for (int i = 0; i < 128; i++) {
            String iAsHex = String.format("%02X", i);
            if (i % 16 == 0) {
                System.out.print("\n" + iAsHex + ": ");
            }

            if (i > 0) {
                try {
                    I2CDevice device = bus.getDevice(i);
                    device.write((byte) 0);
                    System.out.print(iAsHex + " ");
                } catch (Exception ignore) {
                    System.out.print("-- ");
                }
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();
    }
}
