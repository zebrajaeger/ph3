package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.Position;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class PositionGeneratorSquare extends PositionGenerator{

  public PanoMatrix calculatePano(Position currentPosDeg, Image image, Pano pano) {
    PanoMatrix result = new PanoMatrix();
    setYPositions(image, pano, result);
    setXPositions(currentPosDeg, image, pano, result);
    return result;
  }

  private void setXPositions(Position currentPosDeg, Image image, Pano pano, PanoMatrix result) {
    double lImage = Math.abs(image.getWidth());

    if (pano.getFieldOfViewPartial().isFullX()) {
      // full range x
      double startPos = currentPosDeg.getX();
      final ArrayList<Double> positions = calcHFull(startPos, lImage,
          pano.getHorizontalMinimumOverlap());
      result.setAllXPositions(positions);

    } else {
      // Partial range y
      double lPano = Math.abs(pano.getFieldOfViewPartial().getHorizontal().getSize());
      double startPos = Math.min(
          pano.getFieldOfViewPartial().getHorizontal().getFrom(),
          pano.getFieldOfViewPartial().getHorizontal().getTo());

      result.setAllXPositions(
          new MatrixCalculatorPartial().calculatePositions(
              startPos,
              lImage,
              lPano,
              pano.getHorizontalMinimumOverlap()));
    }
  }
}
