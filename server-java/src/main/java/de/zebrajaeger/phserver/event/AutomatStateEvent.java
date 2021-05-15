package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.RobotService;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AutomatStateEvent {
    private RobotService.State state;
    private Command currentCommand;
    private Exception error;

    public AutomatStateEvent(RobotService.State state) {
        this.state = state;
    }

    public AutomatStateEvent(RobotService.State state, Command currentCommand) {
        this.state = state;
        this.currentCommand = currentCommand;
    }

    public AutomatStateEvent(RobotService.State state, Command currentCommand, Exception error) {
        this.state = state;
        this.currentCommand = currentCommand;
        this.error = error;
    }

    public Command getCurrentCommand() {
        return currentCommand;
    }

    public AutomatStateEvent with(Command currentCommand) {
        this.currentCommand = currentCommand;
        return this;
    }

    public AutomatStateEvent with(Exception exception) {
        this.error = exception;
        return this;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
