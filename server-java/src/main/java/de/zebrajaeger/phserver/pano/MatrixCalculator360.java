package de.zebrajaeger.phserver.pano;

import java.util.List;

public class MatrixCalculator360 implements MatrixCalculator {
  // lPano = n * (lImage * (1-overlap))
  // p = n * i * (1-o)

  // n = p / (-i*o +i)
  protected double n(double lImage, double lPano, double overlap) {
    return lPano / (-lImage * overlap + lImage);
  }

  @Override
  public List<Double> calculatePositions(double leftBorder, double lImage, double lPano,
      double overlap) {
    double n = n(lImage, lPano, overlap);
    int imgCount = (int) Math.ceil(n);

    return equidistantPositions(imgCount, leftBorder, lPano / imgCount);
  }
}
