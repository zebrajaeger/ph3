package de.zebrajaeger.phserver.pano;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import org.junit.jupiter.api.Test;

class MatrixCalculatorPartialTest {

  @Test
  void calculatePositionsSpreadPanoSingleLess() {
    MatrixCalculatorPartial matrixCalculator = new MatrixCalculatorPartial(true);

    final List<Double> positions = matrixCalculator.calculatePositions(-10, 30, 20, 0.25);
    assertThat(positions, hasSize(1));
    assertThat(positions.get(0), closeTo(5, 0.01));
  }

  @Test
  void calculatePositionsSpreadPanoSingleEqual() {
    MatrixCalculatorPartial matrixCalculator = new MatrixCalculatorPartial(true);

    final List<Double> positions = matrixCalculator.calculatePositions(-10, 30.0001, 30, 0.25);
    assertThat(positions, hasSize(1));
    assertThat(positions.get(0), closeTo(5, 0.01));
  }

  @Test
  void calculatePositionsSpreadPanoSingleDouble() {
    MatrixCalculatorPartial matrixCalculator = new MatrixCalculatorPartial(true);

    final List<Double> positions = matrixCalculator.calculatePositions(1, 30, 40, 0.25);
    assertThat(positions, hasSize(2));
    assertThat(positions.get(0), closeTo(9.75, 0.01));
    assertThat(positions.get(1), closeTo(32.25, 0.01));
  }

  @Test
  void calculatePositionsSpreadPano4() {
    MatrixCalculatorPartial matrixCalculator = new MatrixCalculatorPartial(true);

    final List<Double> positions = matrixCalculator.calculatePositions(1, 30, 40, 0.25);
    assertThat(positions, hasSize(2));
    assertThat(positions.get(0), closeTo(9.75, 0.01));
    assertThat(positions.get(1), closeTo(32.25, 0.01));
  }

  @Test
  void calculatePositionsSpreadOverlap() {
    MatrixCalculatorPartial matrixCalculator = new MatrixCalculatorPartial(false);

    final List<Double> positions = matrixCalculator.calculatePositions(1, 30, 40, 0.25);
    assertThat(positions, hasSize(2));
    assertThat(positions.get(0), closeTo(16, 0.01));
    assertThat(positions.get(1), closeTo(26, 0.01));
  }
}
