package de.zebrajaeger.phserver.translation;

public class WormGearParameters implements GearParameters {
    @Override
    public double outputToMotor(double outputAngle) {
        return outputAngle * 5 * 60;
    }
}
