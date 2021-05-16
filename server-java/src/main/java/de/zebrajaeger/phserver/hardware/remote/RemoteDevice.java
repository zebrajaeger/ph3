package de.zebrajaeger.phserver.hardware.remote;

import de.zebrajaeger.phserver.hardware.HardwareDevice;

import java.io.IOException;

public class RemoteDevice implements HardwareDevice {
    private final int address;
    private final RemoteService remoteService;

    public RemoteDevice(RemoteService remoteService, int address) {
        this.remoteService = remoteService;
        this.address = address;
    }

    @Override
    public byte[] read(int count) throws IOException {
        return remoteService.read(address, count);
    }

    @Override
    public void write(byte[] data) throws IOException {
        remoteService.write(address, data);
    }
}
