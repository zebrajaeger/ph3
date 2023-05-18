package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Range;
import org.junit.jupiter.api.Test;

class MatrixPanoGeneratorTest {

  @Test
  void calculatePano() {
    // Random example, LOG:
    //<Create Commands>
    //*  currentPosDeg: 'Position(x=-0.0, y=0.0)'
    //*  image: 'Image(width=89.0171191135734, height=29.8670567867036)'
    //*  pano: 'Pano(fieldOfViewPartial=FieldOfView(horizontal=Range(from=-90.04779778393352, to=-0.0), vertical=Range(from=0.0, to=29.399480609418283)), horizontalMinimumOverlap=0.25, verticalMinimumOverlap=0.25)'
    //*  shots: '[Shot(focusTimeMs=1000, triggerTimeMs=1000)]'
    //*  delay: 'Delay(waitAfterMove=1000, waitAfterShot=0, waitBetweenShots=0)'
    //</Create Commands>

    final Position currentPosDeg = new Position(0, 0);
    final Image image = new Image(89.0171191135734, 29.8670567867036);
    final FieldOfViewPartial panoFov = new FieldOfViewPartial(
        new Range(-90.04779778393352, 0.0),
        new Range(0.0, 29.399480609418283),
        false, false);
    final Pano pano = new Pano(panoFov, 0.25, 0.25);

    final MatrixPanoGenerator mpg = new MatrixPanoGenerator(5);
    final PanoMatrix calculatedPano = mpg.calculatePano(currentPosDeg, image, pano);

    // FIXME
//    assertThat(calculatedPano.getMaxXSize(), hasSize(1));
//    assertThat(calculatedPano.getVerticalPositions(), Matchers.contains(
//        Matchers.closeTo(14.9335283933518, 0.001)
//    ));
//
//    assertThat(calculatedPano.getHorizontalPositions(), hasSize(6));
//    assertThat(calculatedPano.getHorizontalPositions(), Matchers.contains(
//        Matchers.closeTo(0, 0.001),
//        Matchers.closeTo(60, 0.001),
//        Matchers.closeTo(120, 0.001),
//        Matchers.closeTo(180, 0.001),
//        Matchers.closeTo(240, 0.001),
//        Matchers.closeTo(300, 0.001)
//    ));
  }
}
