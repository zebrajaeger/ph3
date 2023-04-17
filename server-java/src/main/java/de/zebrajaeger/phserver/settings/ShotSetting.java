package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.zebrajaeger.phserver.data.Shot;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShotSetting {

  private int focusTimeMs = 1000;
  private int triggerTimeMs = 1000;

  public ShotSetting(Shot shot) {
    setAll(shot);
  }

  @JsonIgnore
  public Shot getAll(Shot shot) {
    shot.setFocusTimeMs(this.focusTimeMs);
    shot.setTriggerTimeMs(this.triggerTimeMs);
    return shot;
  }

  @JsonIgnore
  public void setAll(Shot shot) {
    this.focusTimeMs = shot.getFocusTimeMs();
    this.triggerTimeMs = shot.getTriggerTimeMs();
  }
}
