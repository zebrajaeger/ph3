package de.zebrajaeger.phserver.pano;

import java.util.ArrayList;
import java.util.List;

public class MatrixCalculatorPartial {

  public MatrixCalculatorPartial() {
  }

  // Base
  // lPano = (2 * (lImage * (1-lOverlap)))  # left and right image with single overlap
  //       + ((n-2) * (lImage - (1-lOverlap - lOverlap) )) # n-2 center images with double overlap
  //       + ((n-1) * lImage *lOverlap) # n-1 overlap sections

  // o = (-i*n+p) / (-i*n+i)
  protected double overlap(double lImage, double lPano, double n) {
    return (-lImage * n + lPano)
        / (-lImage * n + lImage);
  }

  // n = (-i*o + p) / (-i * o + i)
  protected double n(double lImage, double lPano, double overlap) {
    return (-lImage * overlap + lPano)
        / (-lImage * overlap + lImage);
  }

  public List<Double> calculatePositions(double leftBorder, double lImage, double lPano,
      double overlap) {
//
    return calculatePositionsIncreaseOverlap(leftBorder, lImage, lPano, overlap);
  }

  public List<Double> calculatePositionsIncreaseOverlap(double leftBorder, double lImage,
      double lPano,
      double overlap) {
    double n = n(lImage, lPano, overlap);
    int imgCount = (int) Math.ceil(n);
    double realOverlap = overlap(lImage, lPano, imgCount);

    double first = leftBorder + (lImage / 2d);
    double imgDistance = lImage * (1d - realOverlap);

    return equidistantPositions(imgCount, first, imgDistance);
  }

  private List<Double> equidistantPositions(int imgCount, double first, double imgDistance) {
    List<Double> result = new ArrayList<>(imgCount);
    for (int i = 0; i < imgCount; ++i) {
      result.add(first + (imgDistance * (double) i));
    }
    return result;
  }
}
