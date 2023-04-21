package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Range;
import org.junit.jupiter.api.Test;

class MatrixPanoGeneratorTest {

  @Test
  void calculatePano() {
    final MatrixPanoGenerator mpg = new MatrixPanoGenerator(5, true);
    final Position currentPosDeg = new Position(-50, -50);

    final Image image = new Image(20d, 20d);

    final FieldOfViewPartial panoFov = new FieldOfViewPartial(new Range(-80d, 0d),
        new Range(0d, -30d), true);

    final Pano pano = new Pano(panoFov, 0.25, 0.25);

    final CalculatedPano calculatedPano = mpg.calculatePano(currentPosDeg, image, pano);
    System.out.println(calculatedPano);
  }
}
