package de.zebrajaeger.phserver;

import org.apache.commons.math3.analysis.function.Sigmoid;

public class SigmoidCalculator {
    private double border = 5;
    private final Sigmoid sigmoid = new Sigmoid(0, 1);

    public SigmoidCalculator() {
    }

    public SigmoidCalculator(double border) {
        this.border = border;
    }

    public double value(double x) {
        double y = 0;
        if (x > 0) {
            x = (x - 1) * border;
            y = sigmoid.value(x) * 2;
        } else if (x < 0) {
            x = (-x - 1) * border;
            y = sigmoid.value(x) * -2;
        }
        return y;
    }
}
