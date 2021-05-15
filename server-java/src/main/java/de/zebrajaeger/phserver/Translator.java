package de.zebrajaeger.phserver;

public class Translator {
    protected int stepsPerStepperRevolution = 200 * 256;
    protected double translation = 19d + (38d / 187d);
    protected double beltGear = 5d;
    protected double stepsPerRevolution;

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
