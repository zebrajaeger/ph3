package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Image {
    private double width;
    private double height;

    public Image() {
    }

    public Image(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Image normalized(){
        return new Image(Math.abs(width), Math.abs(height));
    }

    public boolean isComplete(){
        return width!=0d && height !=0d;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
