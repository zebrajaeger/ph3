package de.zebrajaeger.phserver.papywizard.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.zebrajaeger.phserver.data.FieldOfView;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import lombok.Data;

@Data
public class FovSettings {

  private RangeSettings horizontal = new RangeSettings();
  private RangeSettings vertical = new RangeSettings();
  private boolean fullX = false;
  private boolean fullY = false;

  @JsonIgnore
  public void setAll(FieldOfView fov) {
    horizontal.setAll(fov.getHorizontal());
    vertical.setAll(fov.getVertical());
    fullX = false;
    fullY = false;
  }

  @JsonIgnore
  public void setAll(FieldOfViewPartial fov) {
    horizontal.setAll(fov.getHorizontal());
    vertical.setAll(fov.getVertical());
    fullX = fov.isFullX();
    fullY = fov.isFullY();
  }

  @JsonIgnore
  public void getAll(FieldOfView fov) {
    horizontal.getAll(fov.getHorizontal());
    vertical.getAll(fov.getVertical());
  }

  @JsonIgnore
  public void getAll(FieldOfViewPartial fov) {
    horizontal.getAll(fov.getHorizontal());
    vertical.getAll(fov.getVertical());
    fov.setFullX(fullX);
    fov.setFullY(fullY);
  }
}
