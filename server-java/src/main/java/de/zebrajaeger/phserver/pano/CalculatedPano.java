package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Pano;

import java.util.LinkedList;
import java.util.List;

public class CalculatedPano extends Pano {
    private List<Double> horizontalPositions = new LinkedList<>();
    private List<Double> verticalPositions = new LinkedList<>();

    private double horizontalOverlap;
    private double verticalOverlap;

    public CalculatedPano() {
    }

    public CalculatedPano(Pano pano) {
        super(pano);
    }

    public List<Double> getHorizontalPositions() {
        return horizontalPositions;
    }

    public void setHorizontalPositions(List<Double> horizontalPositions) {
        this.horizontalPositions = horizontalPositions;
    }

    public List<Double> getVerticalPositions() {
        return verticalPositions;
    }

    public void setVerticalPositions(List<Double> verticalPositions) {
        this.verticalPositions = verticalPositions;
    }

    public double getHorizontalOverlap() {
        return horizontalOverlap;
    }

    public void setHorizontalOverlap(double horizontalOverlap) {
        this.horizontalOverlap = horizontalOverlap;
    }

    public double getVerticalOverlap() {
        return verticalOverlap;
    }

    public void setVerticalOverlap(double verticalOverlap) {
        this.verticalOverlap = verticalOverlap;
    }
}
