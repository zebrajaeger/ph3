package de.zebrajaeger.phserver.data;

import de.zebrajaeger.phserver.pano.Command;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    public AutomateState getAutomateState() {
        return automateState;
    }

    public void setAutomateState(AutomateState automateState) {
        this.automateState = automateState;
    }

    public PauseState getPauseState() {
        return pauseState;
    }

    public void setPauseState(PauseState pauseState) {
        this.pauseState = pauseState;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command, int commandIndex) {
        this.command = command;
        this.commandIndex = commandIndex;
    }

    public int getCommandCount() {
        return commandCount;
    }

    public int getCommandIndex() {
        return commandIndex;
    }

    public void setCommandCount(int commandCount) {
        this.commandCount = commandCount;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
