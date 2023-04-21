package de.zebrajaeger.phserver.pano;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class MatrixCalculator360Test {

  @Test
  void calculatePositions() {
    MatrixCalculator360 matrixCalculator = new MatrixCalculator360();

    final List<Double> positions = matrixCalculator.calculatePositions(-10, 60, 360d, 0.25);
    assertThat(positions, hasSize(8));
    assertThat(positions.get(0), closeTo(20, 0.01));
    assertThat(positions.get(1), closeTo(65, 0.01));
    assertThat(positions.get(2), closeTo(110, 0.01));
    assertThat(positions.get(3), closeTo(155, 0.01));
    assertThat(positions.get(4), closeTo(200, 0.01));
    assertThat(positions.get(5), closeTo(245, 0.01));
    assertThat(positions.get(6), closeTo(290, 0.01));
    assertThat(positions.get(7), closeTo(335, 0.01));
    // 8: 360 = 20

  }
}
