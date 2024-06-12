package de.zebrajaeger.phserver.translation;

public class BeltGearParameters implements GearParameters {

    private final double tMotor;
    private final double tOutput;

    public BeltGearParameters(double tMotor, double tOutput) {
        this.tMotor = tMotor;
        this.tOutput = tOutput;
    }

    @Override
    public double outputToMotor(double outputAngle) {
        return tOutput * outputAngle / tMotor;
    }
}
