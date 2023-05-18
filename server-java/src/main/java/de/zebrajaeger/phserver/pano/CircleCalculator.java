package de.zebrajaeger.phserver.pano;

import java.util.ArrayList;
import java.util.List;

public class CircleCalculator {

  /**
   * @param x [-1..1]
   * @return [0..1]
   */
  public double getY(double x) {
    return Math.sqrt(1 - (x * x));
  }


  public List<Double> equidistant(double source, double startPos, double lImage, double overlap, double x) {

    double l = source * getY(x);
    double lImageCropped = lImage * (1d - overlap);
    double n = Math.ceil(l / lImageCropped);
    double distance = source / n;
    List<Double> result = new ArrayList<>();
    for (int i = 0; i < n; ++i) {
      result.add(startPos + (distance * i));
    }
    return result;
  }

  private double normalize(double v) {
    if (v > 1) {
      return 2 - v;
    } else if (v < -1) {
      return -2 - v;
    }
    return v;
  }

  public List<Double> positions(
      double source, double startPos,
      double lImage, double overlap,
      double x1, double x2) {

    // make sure, x1 < x2
    if (x1 > x2) {
      double temp = x1;
      x1 = x2;
      x2 = temp;
    }

    // if x is over the pole, it is on the other side, so it has to be modified
    x1 = normalize(x1);
    x2 = normalize(x2);

    double x;
    if (x1 < 0 && x2 > 0) {
      // special case, target is on equator
      x = 1;
    } else {
      // otherwise, we choose the longer side
      x = Math.max(getY(x1), getY(x2));
    }

    return equidistant(source, startPos, lImage, overlap,x);
  }

  public List<Double> rowPositions(double y1, double y2, double lImage, double overlap){
    // make sure, y1 < y2
    if (y1 > y2) {
      double temp = y1;
      y1 = y2;
      y2 = temp;
    }

    double l = y1-y2;


    double lImageCropped = lImage * (1d - overlap);
    double n = Math.ceil(l / lImageCropped);
    double distance = l / n;
    List<Double> result = new ArrayList<>();
    for (int i = 0; i < n; ++i) {
      result.add(distance * i);
    }

    return result;
  }

}
