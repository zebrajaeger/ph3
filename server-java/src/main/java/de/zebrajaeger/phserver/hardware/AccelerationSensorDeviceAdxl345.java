package de.zebrajaeger.phserver.hardware;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * https://www.analog.com/media/en/technical-documentation/data-sheets/ADXL345.pdf
 */
public class AccelerationSensorDeviceAdxl345 implements AccelerationSensor {
    private final HardwareDevice hardwareDevice;
    private boolean initialized = false;

    public AccelerationSensorDeviceAdxl345(HardwareDevice hardwareDevice) {
        this.hardwareDevice = hardwareDevice;
    }

    private void init() throws IOException {
        if (!initialized) {
            int i = Byte.toUnsignedInt(readRegister((byte) 0));
            // TODO ensure that  i == 11100101
//            System.out.println(Integer.toBinaryString(i));

            // Select Bandwidth rate register
            // Normal mode, Output data rate = 100 Hz
            writeRegister((byte) 0x2C, (byte)0x0A);

            // Select Power control register
            // Auto-sleep disable
            writeRegister((byte) 0x2D, (byte) 0x08);

            // Select Data format register
            // Self test disabled, 4-wire interface, Full resolution, range = +/-2g
            writeRegister((byte)0x31, (byte)0x08);
            initialized = true;
        }
    }

    float alpha = 0.5f;

    double fXg = 0f;
    double fYg = 0f;
    double fZg = 0f;

    @Override
    public void foo() throws IOException {
        init();
        Acceleration acc = readAcceleration();
        fXg = acc.getX() * alpha + (fXg * (1.0 - alpha));
        fYg = acc.getY() * alpha + (fYg * (1.0 - alpha));
        fZg = acc.getZ() * alpha + (fZg * (1.0 - alpha));

        //Roll and Pitch Equations
        double roll  =  ((Math.atan2(-fYg, fZg)*180.0)/Math.PI);
        double  pitch = (Math.atan2(fXg, Math.sqrt(fYg*fYg + fZg*fZg))*180.0)/Math.PI;
        // TODO System.out.println(roll + "/" + pitch);
    }

    private byte readRegister(byte adr) throws IOException {
        hardwareDevice.write(new byte[]{adr});
        return hardwareDevice.read(1)[0];
    }

    private void writeRegister(byte adr, byte value) throws IOException {
        hardwareDevice.write(new byte[]{adr, value});
    }

    private Acceleration readAcceleration() throws IOException {
        hardwareDevice.write(new byte[]{0x32});
        ByteBuffer buffer = ByteBuffer.wrap(hardwareDevice.read(6)).order(ByteOrder.LITTLE_ENDIAN);
        return new Acceleration(buffer.getShort(), buffer.getShort(), buffer.getShort());
    }
}
