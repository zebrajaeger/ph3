package de.zebrajaeger.phserver.hardware.fake;

import de.zebrajaeger.phserver.hardware.PowerGauge;

public class FakePowerGauge implements PowerGauge {
    @Override
    public int readVoltageInMillivolt() {
        return (int) (15000 + (Math.random() * 6000));
    }

    @Override
    public int readCurrentInMilliampere() {
        return (int) (400 + (Math.random() * 300));
    }

    @Override
    public int readPower() {
        return (int) (9 + (Math.random() * 3));
    }
}
