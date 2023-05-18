package de.zebrajaeger.phserver.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class FieldOfViewPartial extends FieldOfView {

  private boolean fullX = false;
  private boolean fullY = false;

  public FieldOfViewPartial() {
  }

  public FieldOfViewPartial(FieldOfViewPartial fov) {
    super(fov);
    this.fullX = fov.fullX;
    this.fullY = fov.fullY;
  }

  public FieldOfViewPartial(Range horizontal, Range vertical, boolean fullX, boolean fullY) {
    super(horizontal, vertical);
    this.fullX = fullX;
    this.fullY = fullY;
  }

  public FieldOfViewPartial normalize() {
    return new FieldOfViewPartial(
        getHorizontal() == null ? null : getHorizontal().normalize(),
        getVertical() == null ? null : getVertical().normalize(),
        fullX, fullY);
  }

  @Override
  public boolean isComplete() {
    boolean x = fullX || super.getHorizontal().isComplete();
    boolean y = fullY || super.getVertical().isComplete();
    return x && y;
  }
}
