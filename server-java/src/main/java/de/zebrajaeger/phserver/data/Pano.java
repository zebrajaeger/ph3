package de.zebrajaeger.phserver.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Pano {

  private FieldOfViewPartial fieldOfViewPartial;
  private double horizontalMinimumOverlap = 0.25d;
  private double verticalMinimumOverlap = 0.25d;

  public Pano(Pano pano) {
    this.fieldOfViewPartial = new FieldOfViewPartial(pano.fieldOfViewPartial);
    this.horizontalMinimumOverlap = pano.horizontalMinimumOverlap;
    this.verticalMinimumOverlap = pano.verticalMinimumOverlap;
  }

  public Pano(FieldOfViewPartial fieldOfViewPartial, double horizontalMinimumOverlap,
      double verticalMinimumOverlap) {
    this.fieldOfViewPartial = fieldOfViewPartial;
    this.horizontalMinimumOverlap = horizontalMinimumOverlap;
    this.verticalMinimumOverlap = verticalMinimumOverlap;
  }
}
