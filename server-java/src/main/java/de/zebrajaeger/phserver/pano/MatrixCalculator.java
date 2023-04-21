package de.zebrajaeger.phserver.pano;

import java.util.ArrayList;
import java.util.List;

public interface MatrixCalculator {

  List<Double> calculatePositions(double leftBorder, double lImage, double lPano, double overlap);

  default List<Double> equidistantPositions(int imgCount, double first, double imgDistance) {
    List<Double> result = new ArrayList<>(imgCount);
    for (int i = 0; i < imgCount; ++i) {
      result.add(first + (imgDistance * (double) i));
    }
    return result;
  }
}
