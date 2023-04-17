package de.zebrajaeger.phserver.data;

import lombok.Data;

@Data
public class FieldOfView {

  private Range horizontal = new Range();
  private Range vertical = new Range();

  public FieldOfView() {
  }

  public FieldOfView(FieldOfView fov) {
    if (fov.horizontal != null) {
      this.horizontal = new Range(fov.horizontal);
    }
    if (fov.vertical != null) {
      this.vertical = new Range(fov.vertical);
    }
  }

  public FieldOfView(double hFrom, double hTo, double vFrom, double vTo) {
    this.horizontal = new Range(hFrom, hTo);
    this.vertical = new Range(vFrom, vTo);
  }

  public FieldOfView(Range horizontal, Range vertical) {
    this.horizontal = horizontal;
    this.vertical = vertical;
  }

  public FieldOfView normalize() {
    return new FieldOfView(
        getHorizontal() == null ? null : getHorizontal().normalize(),
        getVertical() == null ? null : getVertical().normalize());
  }

  public boolean isComplete() {
    return horizontal.isComplete() && vertical.isComplete();
  }
}
