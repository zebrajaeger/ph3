package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.Position;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class PositionGeneratorSparseSquare extends PositionGenerator {

  public PanoMatrix calculatePano(Position currentPosDeg, Image image, Pano pano) {
    PanoMatrix result = new PanoMatrix();
    setYPositions(image, pano, result);
    setXPositions(currentPosDeg, image, pano, result);
    return result;
  }

  private double sqrNormalized90(double deg) {
    return (deg * deg) / 8100d; // (deg / 90d) * (deg / 90d)
  }

  private void setXPositions(Position currentPosDeg, Image image, Pano pano, PanoMatrix result) {
    double lImageX = Math.abs(image.getWidth());
    double lImageY = Math.abs(image.getHeight());

    int yIndex = 0;
    for (Double y : result.getYPositions()) {
      double y1 = y - lImageY / 2;
      if (y1 > 90) {
        // special case top pole
        y1 = 180 - y1; // 90 - (90-y1);
      }
      if (y1 < -90) {
        // special case bottom pole
        y1 = -180 - y1; // -90 + (-90-y1);
      }
      double y2 = y + lImageY / 2;
      if (y2 > 90) {
        // special case top pole
        y2 = 180 - y2; // 90 - (90-y1);
      }
      if (y1 < -90) {
        // special case bottom pole
        y2 = -180 - y2; // -90 + (-90-y1);
      }

//      final Double lPano = pano.getFieldOfViewPartial().getHorizontal().getSize();

      double lRelativeX;
      if ((y1 > 0 && y2 > 0) || (y1 < 0 && y2 < 0)) {
        double lRelativeX1 = Math.sqrt(1 - sqrNormalized90(y1));
        double lRelativeX2 = Math.sqrt(1 - sqrNormalized90(y2));
        lRelativeX = Math.max(lRelativeX1, lRelativeX2);
      } else {
        // special case: equator
        lRelativeX = 1;
      }

      if (pano.getFieldOfViewPartial().isFullX()) {
        final ArrayList<Double> xPositions = calcHFull(
            currentPosDeg.getX(),
            lImageX,
            lRelativeX * 360,
            360,
            pano.getHorizontalMinimumOverlap());
        result.setXPositions(yIndex, xPositions);

      } else {
        final double lPano = pano.getFieldOfViewPartial().getHorizontal().getSize();
        double lPanoReduced = lRelativeX * lPano;
//        double offset = (lPano - lPanoReduced)/2;
        double offset = 0;
        double startPos = Math.min(
            pano.getFieldOfViewPartial().getHorizontal().getFrom(),
            pano.getFieldOfViewPartial().getHorizontal().getTo());
        final List<Double> xPositions = new MatrixCalculatorPartialSparse().calculatePositions(
            startPos  + offset,
            lImageX,
            lPanoReduced,
            lPano,
            pano.getHorizontalMinimumOverlap());
        result.setXPositions(yIndex, xPositions);
      }
      yIndex++;
    }
  }
}
