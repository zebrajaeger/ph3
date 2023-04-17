package de.zebrajaeger.phserver.data;

import lombok.Data;

@Data
public class JoystickPosition {

  private final JoystickAxis x = new JoystickAxis();
  private final JoystickAxis y = new JoystickAxis();

  public void reset() {
    x.reset();
    y.reset();
  }

  public void setCenterWithRawValues(float rawValueX, float rawValueY) {
    x.setRawValueAsCenter(rawValueX);
    y.setRawValueAsCenter(rawValueY);
  }

  public void updateWithRawValues(float rawValueX, float rawValueY) {
    x.setRawValue(rawValueX);
    y.setRawValue(rawValueY);
  }
}
