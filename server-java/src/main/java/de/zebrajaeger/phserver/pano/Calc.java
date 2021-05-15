package de.zebrajaeger.phserver.pano;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

public class Calc {
    private boolean partial = false;
    private Double targetStartPoint;
    private Double targetSize;
    private Double sourceSize;
    private Double overlap;

    public static class Result {
        private int n;
        private double overlap;
        private List<Double> startPositions;

        public int getN() {
            return n;
        }

        public double getOverlap() {
            return overlap;
        }

        public List<Double> getStartPositions() {
            return startPositions;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    public void reset() {
        partial = false;
        targetStartPoint = null;
        targetSize = null;

        sourceSize = null;
        overlap = null;
    }

    public Result calc() {
        Result r = new Result();
        r.n = this.calcN();
        r.overlap = this.calcOverlap(r.n);
        r.startPositions = this.calcStartPositions(r.n, r.overlap);
        return r;
    }

    private int calcN() {
        double b = sourceSize;
        double d = targetSize;
        double o = overlap;

        double n;
        if (partial) {
            n = (-b * o + d) / (-b * o + b);
        } else {
            n = d / (-b * o + b);
        }
        return (int) Math.ceil(n);
    }

    private double calcOverlap(int n) {
        double b = this.sourceSize;
        double d = this.targetSize;

        if (partial) {
            double overlap;
            overlap = (-b * n + d) / (-b * n + b);
            return overlap;
        } else {
//            double allSourceImages = this.sourceSize * n;
//            double overlapAll = allSourceImages - this.targetSize;
//            double overlapPerSource = overlapAll / n;
//            double result = overlapPerSource / this.sourceSize;
//            return result;
            return (b * n - d) / (b * n);
        }
    }

    public List<Double> calcStartPositions(int n, double overlap) {
        List<Double> result = new LinkedList<>();
        for (int i = 0; i < n; ++i) {
            result.add(targetStartPoint + (sourceSize * (1 - overlap) * i));
        }
        return result;
    }

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    public Double getTargetStartPoint() {
        return targetStartPoint;
    }

    public void setTargetStartPoint(Double targetStartPoint) {
        this.targetStartPoint = targetStartPoint;
    }

    public Double getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(Double targetSize) {
        this.targetSize = targetSize;
    }

    public Double getSourceSize() {
        return sourceSize;
    }

    public void setSourceSize(Double sourceSize) {
        this.sourceSize = sourceSize;
    }

    public Double getOverlap() {
        return overlap;
    }

    public void setOverlap(Double overlap) {
        Assert.state(overlap >= 0d, "Overlap must be greater or equal zero ");
        Assert.state(overlap < 1d, "Overlap must be less than zero ");
        this.overlap = overlap;
    }
}
