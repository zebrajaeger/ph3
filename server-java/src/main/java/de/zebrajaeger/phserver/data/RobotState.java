package de.zebrajaeger.phserver.data;

import de.zebrajaeger.phserver.pano.Command;
import lombok.Data;

@Data
public class RobotState {

  private AutomateState automateState;
  private PauseState pauseState;
  private Command command;
  private int commandIndex = 0;
  private int commandCount = 0;

  public RobotState(AutomateState automateState, PauseState pauseState, Command command) {
    this.automateState = automateState;
    this.pauseState = pauseState;
    this.command = command;
  }

  public void setCommand(Command command, int commandIndex) {
    this.command = command;
    this.commandIndex = commandIndex;
  }
}
