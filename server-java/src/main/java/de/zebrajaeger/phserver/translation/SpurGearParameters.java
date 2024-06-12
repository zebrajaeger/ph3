package de.zebrajaeger.phserver.translation;

/**
 * <a href="https://www.omc-stepperonline.com/download/11HS12-0674D-PG27.pdf">
 * https://www.omc-stepperonline.com/download/11HS12-0674D-PG27.pdf
 * </a>
 */
public class SpurGearParameters implements GearParameters {

    @Override
    public double outputToMotor(double outputAngle) {
        return outputAngle * 5 * (26d + (103d / 121d));
    }
}
