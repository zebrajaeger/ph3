package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.zebrajaeger.phserver.data.JoystickAxis;
import lombok.Data;

@Data
public class JoystickAxisSettings {

  private float rawMin = Float.MAX_VALUE;
  private float rawCenter = 0f;
  private float rawMax = -Float.MAX_VALUE;

  @JsonIgnore
  public void setAll(JoystickAxis axis) {
    rawMin = axis.getAutoRange().getRawMin();
    rawCenter = axis.getAutoRange().getRawCenter();
    rawMax = axis.getAutoRange().getRawMax();
  }

  @JsonIgnore
  public void getAll(JoystickAxis axis) {
    axis.getAutoRange().setAll(rawMin, rawCenter, rawMax);
  }
}
