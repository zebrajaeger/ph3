package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.AutomateState;
import de.zebrajaeger.phserver.data.PauseState;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.RawPosition;
import de.zebrajaeger.phserver.data.RobotState;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.event.MovementStoppedEvent;
import de.zebrajaeger.phserver.event.RobotStateEvent;
import de.zebrajaeger.phserver.event.ShotDoneEvent;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.GoToPosCommand;
import de.zebrajaeger.phserver.pano.TakeShotCommand;
import de.zebrajaeger.phserver.pano.WaitCommand;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class RobotService {
    private static final Logger LOG = LoggerFactory.getLogger(RobotService.class);

    private final HardwareService hardwareService;
    private final Translator translator;
    private final ApplicationEventPublisher applicationEventPublisher;

    private List<Command> commands = new LinkedList<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private Integer currentCommandIndex = null;
    private final RobotState robotState = new RobotState(AutomateState.STOPPED, PauseState.RUNNING, null);

    @Autowired
    public RobotService(HardwareService hardwareService, Translator translator, ApplicationEventPublisher applicationEventPublisher) {
        this.hardwareService = hardwareService;
        this.translator = translator;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private RobotService setAutomateState(AutomateState automateState) {
        LOG.debug("Set state to '{}'", automateState);
        this.robotState.setAutomateState(automateState);
        return this;
    }

    private RobotService setPauseState(PauseState state) {
        LOG.debug("Set pause-state to '{}'", state);
        this.robotState.setPauseState(state);
        return this;
    }

    private RobotService setCommand(Command command, int commandIndex) {
        LOG.debug("Set command #{} to '{}'", commandIndex, command);
        this.robotState.setCommand(command, commandIndex);
        return this;
    }

    private void sendUpdate() {
        RobotStateEvent event = new RobotStateEvent(this.robotState);
        LOG.debug("State changed to '{}'", event);
        applicationEventPublisher.publishEvent(event);
    }

    private void sendUpdate(Exception error) {
        RobotStateEvent event = new RobotStateEvent(this.robotState, error);
        LOG.debug("State changed to '{}'", event);
        applicationEventPublisher.publishEvent(event);
    }

    public void start(List<Command> commands) {
        if (this.robotState.getAutomateState() == AutomateState.STOPPED || this.robotState.getAutomateState() == AutomateState.STOPPED_WITH_ERROR) {
            this.commands = commands;
            this.robotState.setCommandCount(commands.size());
            setAutomateState(AutomateState.STARTED).sendUpdate();
            next();
        } else {
            throw new IllegalStateException("Already running");
        }
    }

    public void stop() {
        if (this.robotState.getAutomateState() != AutomateState.STOPPED && this.robotState.getAutomateState() != AutomateState.STOPPED_WITH_ERROR) {
            setAutomateState(AutomateState.STOPPING).setPauseState(PauseState.RUNNING).sendUpdate();
        }
    }

    public void PauseResume() {
        if (this.robotState.getAutomateState() != AutomateState.STOPPED && this.robotState.getAutomateState() != AutomateState.STOPPED_WITH_ERROR) {
            if (this.robotState.getPauseState() == PauseState.PAUSING) {
                setPauseState(PauseState.RUNNING).sendUpdate();
                next();
            } else if (this.robotState.getPauseState() == PauseState.PAUSE_REQUESTED) {
                setPauseState(PauseState.RUNNING).sendUpdate();
            } else if (this.robotState.getPauseState() == PauseState.RUNNING) {
                setPauseState(PauseState.PAUSE_REQUESTED).sendUpdate();
            } else {
                throw new IllegalStateException("Unknown pause state" + this.robotState.getPauseState());
            }
        }
    }

    private void next() {
        // Command state
        if (this.robotState.getAutomateState() == AutomateState.STOPPED || this.robotState.getAutomateState() == AutomateState.STOPPED_WITH_ERROR) {
            return;
        }

        // PAUSE state
        if (this.robotState.getPauseState() == PauseState.PAUSE_REQUESTED) {
            setPauseState(PauseState.PAUSING).sendUpdate();
            return;
        }
        if (this.robotState.getPauseState() == PauseState.PAUSING) {
            return;
        }

        // prepare next command index
        if (this.robotState.getAutomateState() == AutomateState.STARTED) {
            currentCommandIndex = 0;
        } else {
            currentCommandIndex++;
        }

        // stop on last command
        if (currentCommandIndex >= commands.size()) {
            setAutomateState(AutomateState.STOPPED).setCommand(null, -1).sendUpdate();
            return;
        }

        Command currentCommand = commands.get(currentCommandIndex);
        setCommand(currentCommand, currentCommandIndex).sendUpdate();

        // exec command
        if (WaitCommand.class.equals(currentCommand.getClass())) {
            setAutomateState(AutomateState.CMD_DELAY).sendUpdate();
            executorService.schedule(this::onTimer, ((WaitCommand) currentCommand).getTimeMs(), TimeUnit.MILLISECONDS);

        } else if (GoToPosCommand.class.equals(currentCommand.getClass())) {
            setAutomateState(AutomateState.CMD_MOVE).sendUpdate();

            RawPosition rawPosition = translatePos(((GoToPosCommand) currentCommand).getPosition());
            try {
                hardwareService.getPanoHead().setTargetPos(0, rawPosition.getX());
                hardwareService.getPanoHead().setTargetPos(1, rawPosition.getY());
            } catch (IOException e) {
                setAutomateState(AutomateState.STOPPED_WITH_ERROR).sendUpdate(e);
            }

        } else if (TakeShotCommand.class.equals(currentCommand.getClass())) {
            setAutomateState(AutomateState.CMD_SHOT).sendUpdate();
            Shot shot = ((TakeShotCommand) currentCommand).getShot();
            try {
                hardwareService.getPanoHead().startShot(shot.getFocusTimeMs(), shot.getTriggerTimeMs());
            } catch (IOException e) {
                setAutomateState(AutomateState.STOPPED_WITH_ERROR).sendUpdate(e);
            }

        } else {
            setAutomateState(AutomateState.STOPPED_WITH_ERROR).sendUpdate(new UnsupportedOperationException("Unknown Command"));
        }
    }

    @EventListener
    public void onMovementStopped(MovementStoppedEvent event) {
        LOG.debug("onMovementStopped()");
        next();
    }

    @EventListener
    public void onShotDone(ShotDoneEvent event) {
        LOG.debug("onShotDone()");
        next();
    }

    private void onTimer() {
        LOG.debug("onTimer()");
        next();
    }

    private RawPosition translatePos(Position position) {
        return new RawPosition(
                (int) translator.degToSteps(position.getX()),
                (int) translator.degToSteps(position.getY()));
    }

    public RobotState getRobotState() {
        return robotState;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
