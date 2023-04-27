package de.zebrajaeger.phserver.papywizard;

import de.zebrajaeger.phserver.data.ShotPosition;
import de.zebrajaeger.phserver.pano.Positions;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

class PapywizardGeneratorTest {

  //  <?xml version="1.0" encoding="utf-8"?>
//	<papywizard>
//		<shoot>
//			<pict bracket="1">
//						<position pitch="1" yaw="0"/>
//				</pict>
//				<pict bracket="1">
//						<position pitch="1" yaw="120"/>
//				</pict>
//				<pict bracket="1">
//						<position pitch="1" yaw="240"/>
//				</pict>
//				<pict bracket="1">
//						<position pitch="2" yaw="0"/>
//				</pict>
//				<pict bracket="1">
//						<position pitch="2" yaw="120"/>
//				</pict>
//				<pict bracket="1">
//						<position pitch="2" yaw="240"/>
//				</pict>
//			</shoot>
//	</papywizard>
  @Test
  public void foo() {
    final Positions positions = createPositions(2, 2);
    final PapywizardGenerator g = new PapywizardGenerator();
    final String xml = g.generate(positions);
    System.out.println(xml);
  }

  @Test
  public void bar() throws IOException {
    final Positions positions = createPositions(
        new double[]{0.0, 36.0, 72.0, 108.0, 144.0, 180.0, 216.0, 252.0, 288.0, 324.0},
        new double[]{-66.01559729916899, -40.272512119113586, -14.529426939058183,
            11.21365824099722, 36.95674342105262, 62.699828601108024});
    final PapywizardGenerator g = new PapywizardGenerator();
    final String xml = g.generate(positions);
    System.out.println(xml);
//    File f = new File("C:/prj/ph3/temp/papywizard-60-10.xml");
//    FileUtils.write(f,xml, Charset.defaultCharset());
  }

  private Positions createPositions(int xl, int yl) {
    Positions positions = new Positions(2, 2);
    positions.add(createShotPosition(0, 0, xl, yl));
    positions.add(createShotPosition(1, 0, xl, yl));
    positions.add(createShotPosition(0, 1, xl, yl));
    positions.add(createShotPosition(1, 1, xl, yl));
    return positions;
  }

  private ShotPosition createShotPosition(int x, int y, int xl, int yl) {
    return new ShotPosition(x, y, 0, x, 2, y, 2);
  }

  private Positions createPositions(double[] x, double[] y) {
    Positions positions = new Positions(x.length, y.length);
    int yi = 0;
    int index = 0;
    for (double yPos : y) {
      int xi = 0;
      for (double xPos : x) {
        positions.add(new ShotPosition(xPos, yPos, index, xi, x.length, yi, y.length));

        xi++;
        index++;
      }
      yi++;
    }
    return positions;
  }
}
