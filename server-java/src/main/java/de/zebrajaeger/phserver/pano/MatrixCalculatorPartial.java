package de.zebrajaeger.phserver.pano;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Precision;

@AllArgsConstructor
public class MatrixCalculatorPartial implements MatrixCalculator{
  private boolean spreadPano;

  // Base
  // lPano = (2 * (lImage * (1-lOverlap)))  # left and right image with single overlap
  //       + ((n-2) * (lImage - (1-lOverlap - lOverlap) )) # n-2 center images with double overlap
  //       + ((n-1) * lImage *lOverlap) # n-1 overlap sections

  // p = (2 * (i * (1-o))) + ((n-2) * (i * (1-o-o))) + ((n-1) * (i*o))
  protected double lPano(double lImage, double n, double overlap) {
    return (2 * (lImage * (1 - overlap)))
        + ((n - 2) * (lImage * (1 - overlap - overlap)))
        + ((n - 1) * (lImage * overlap));
  }

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

  @Override
  public List<Double> calculatePositions(double leftBorder, double lImage, double lPano,
      double overlap) {

    return spreadPano
        ? calculatePositionsIncreaseLPano(leftBorder, lImage, lPano, overlap)
        : calculatePositionsIncreaseOverlap(leftBorder, lImage, lPano, overlap);
  }

  public List<Double> calculatePositionsIncreaseLPano(double leftBorder, double lImage, double lPano,
      double overlap) {
    // lPano <= lImage -> only one position in the center
    if (lPano < lImage || Precision.equals(lPano, lImage, 0.1d)) {
      return List.of(leftBorder + (lImage / 2));
    }

    double n = n(lImage, lPano, overlap);
    int imgCount = (int) Math.ceil(n);
    double realLPano = lPano(lImage, imgCount, overlap);
    double leftOffset = ((realLPano - lPano) / 2d);

    double first = leftBorder - leftOffset + (lImage / 2d);
    double imgDistance = lImage * (1d - overlap);

    return equidistantPositions(imgCount, first, imgDistance);
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

}
