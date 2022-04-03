package de.zebrajaeger.phserver;

public abstract class StepsToDeg implements Translator {

    // https://www.ti.com/lit/ds/symlink/drv8825.pdf
    // https://www.pololu.com/product/2133/pictures#lightbox-picture0J4222
    private static final double STEPPER_MICROSTEPS = 8;
    // https://www.omc-stepperonline.com/download/11HS12-0674D-PG27.pdf
    private static final double STEPPER_STEPS = 200;
    private static final double STEPPER_GEARBOX_RATIO = 26d + (103d / 121d);
    // 3D Printed Gearbox 15:85 = 1:5
    private static final double GEARBOX_RATIO = 85d/15d;

    protected static double RATIO = STEPPER_STEPS * STEPPER_MICROSTEPS * STEPPER_GEARBOX_RATIO * GEARBOX_RATIO;

    public static final Translator INSTANCE = new StepsToDeg() {
        @Override
        public double translate(double value) {
            return value * 360d / RATIO;
        }
    };
    public static final Translator REVERSE = new StepsToDeg() {
        @Override
        public double translate(double value) {
            return (value / 360d) * RATIO;
        }
    };
}
