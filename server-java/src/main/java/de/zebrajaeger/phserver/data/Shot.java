package de.zebrajaeger.phserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shot {

  private int focusTimeMs = 1000;
  private int triggerTimeMs = 1000;
}
