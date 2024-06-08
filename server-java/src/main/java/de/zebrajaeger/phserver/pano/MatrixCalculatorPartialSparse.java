package de.zebrajaeger.phserver.pano;

import java.util.ArrayList;
import java.util.List;

//@AllArgsConstructor
public class MatrixCalculatorPartialSparse {

  public MatrixCalculatorPartialSparse() {
  }



  // n = (-i*o + p) / (-i * o + i)
  protected double n(double lImage, double lPano, double overlap) {
    return (-lImage * overlap + lPano)
        / (-lImage * overlap + lImage);
  }

  public List<Double> calculatePositions(
      double startPosInTargetPano,
      double lImage,
      double lPanoToCalculate,
      double lPanoTarget,
      double overlap) {
    double n = n(lImage, lPanoToCalculate, overlap);
    int imgCount = (int) Math.ceil(n);
    double first = startPosInTargetPano + (lImage / 2d);
    double last = startPosInTargetPano + lPanoTarget - (lImage / 2d);

    return equidistantPositions2(imgCount, first, last);
  }

  private List<Double> equidistantPositions2(int imgCount, double firstPosition,
      double lastPosition) {
    List<Double> result = new ArrayList<>(imgCount);
    double d = (lastPosition - firstPosition) / (imgCount - 1);
    for (int i = 0; i < imgCount; ++i) {
      result.add(firstPosition + (d * (double) i));
    }
    return result;
  }
}
