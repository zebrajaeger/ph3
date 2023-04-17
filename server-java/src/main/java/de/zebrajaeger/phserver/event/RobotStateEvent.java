package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.RobotState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RobotStateEvent {

  private final RobotState robotState;
  private Exception error;
}
