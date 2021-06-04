package de.zebrajaeger.phserver;

public class Translator {
    private final int stepsPerStepperRevolution = 200 * 256;
    private final double translation = 19d + (38d / 187d);
    private final double beltGear = 5d;
    private final double stepsPerRevolution;

    public Translator() {
        stepsPerRevolution = translation * beltGear * stepsPerStepperRevolution;
    }

    public double stepsToRevolution(int steps) {
        return steps / stepsPerRevolution;
    }

    public double stepsToDeg(int steps) {
        return steps * 360d / stepsPerRevolution;
    }

    public double revolutionToSteps(double revolutions) {
        return revolutions * this.stepsPerRevolution;
    }

    public double degToSteps(double revolutions) {
        return revolutions * stepsPerRevolution / 360d;
    }

    public int getStepsPerStepperRevolution() {
        return stepsPerStepperRevolution;
    }

    public double getTranslation() {
        return translation;
    }

    public double getBeltGear() {
        return beltGear;
    }

    public double getStepsPerRevolution() {
        return stepsPerRevolution;
    }
}
