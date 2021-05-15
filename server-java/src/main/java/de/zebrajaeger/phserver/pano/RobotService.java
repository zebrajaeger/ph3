package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.HardwareService;
import de.zebrajaeger.phserver.Translator;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.RawPosition;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.event.AutomatStateEvent;
import de.zebrajaeger.phserver.event.MovementStoppedEvent;
import de.zebrajaeger.phserver.event.ShotDoneEvent;
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
    private State state = State.STOPPED;
    private PauseState pause = PauseState.RUNNING;

    public enum State {
        STOPPED, STOPPED_WITH_ERROR, STARTED, CMD_DELAY, CMD_SHOT, CMD_MOVE, STOPPING
    }

    public enum PauseState {
        RUNNING, PAUSE_REQUESTED, PAUSING
    }

    @Autowired
    public RobotService(HardwareService hardwareService, Translator translator, ApplicationEventPublisher applicationEventPublisher) {
        this.hardwareService = hardwareService;
        this.translator = translator;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private void setState(State state) {
        LOG.debug("Set state to '{}'", state);
        this.state = state;
    }

    private void setPauseState(PauseState state) {
        LOG.debug("Set pause-state to '{}'", state);
        this.pause = state;
    }

    public void start(List<Command> commands) {
        if (state == State.STOPPED || state == State.STOPPED_WITH_ERROR) {
            this.commands = commands;
            setState(State.STARTED);
            next();
        } else {
            throw new IllegalStateException("Already running");
        }
    }

    public void stop() {
        if (state != State.STOPPED && state != State.STOPPED_WITH_ERROR) {
            setState(State.STOPPING);
            setPauseState(PauseState.RUNNING);
        }
    }

    public void PauseResume() {
        if (state != State.STOPPED && state != State.STOPPED_WITH_ERROR) {
            if (pause == PauseState.PAUSING) {
                setPauseState(PauseState.RUNNING);
                next();
            } else if (pause == PauseState.PAUSE_REQUESTED) {
                setPauseState(PauseState.RUNNING);
            } else if (pause == PauseState.RUNNING) {
                setPauseState(PauseState.PAUSE_REQUESTED);
            } else {
                throw new IllegalStateException("Unknown pause state" + pause);
            }
        }
    }

    private void next() {
        // Command state
        if (state == State.STOPPED || state == State.STOPPED_WITH_ERROR) {
            return;
        }

        // PAUSE state
        if (pause == PauseState.PAUSE_REQUESTED) {
            setPauseState(PauseState.PAUSING);
            return;
        }
        if (pause == PauseState.PAUSING) {
            return;
        }

        // prepare next command index
        if (state == State.STARTED) {
            currentCommandIndex = 0;
            applicationEventPublisher.publishEvent(new AutomatStateEvent(state));
        } else {
            currentCommandIndex++;
        }

        // stop on last command
        if (currentCommandIndex >= commands.size()) {
            setState(State.STOPPED);
            applicationEventPublisher.publishEvent(new AutomatStateEvent(state));
            return;
        }

        // exec command
        Command currentCommand = commands.get(currentCommandIndex);

        if (WaitCommand.class.equals(currentCommand.getClass())) {
            setState(State.CMD_DELAY);
            applicationEventPublisher.publishEvent(new AutomatStateEvent(state, currentCommand));
            executorService.schedule(this::onTimer, ((WaitCommand) currentCommand).getTimeMs(), TimeUnit.MILLISECONDS);

        } else if (GoToPosCommand.class.equals(currentCommand.getClass())) {
            setState(State.CMD_MOVE);
            applicationEventPublisher.publishEvent(new AutomatStateEvent(state, currentCommand));

            RawPosition rawPosition = translatePos(((GoToPosCommand) currentCommand).getPosition());
            try {
                hardwareService.getPanoHead().setTargetPos(0, rawPosition.getX());
                hardwareService.getPanoHead().setTargetPos(1, rawPosition.getY());
            } catch (IOException e) {
                setState(State.STOPPED_WITH_ERROR);
                applicationEventPublisher.publishEvent(new AutomatStateEvent(state, currentCommand, e));
            }

        } else if (TakeShotCommand.class.equals(currentCommand.getClass())) {
            setState(State.CMD_SHOT);
            applicationEventPublisher.publishEvent(new AutomatStateEvent(state, currentCommand));
            Shot shot = ((TakeShotCommand) currentCommand).getShot();
            try {
                hardwareService.getPanoHead().startShot(shot.getFocusTimeMs(), shot.getTriggerTimeMs());
            } catch (IOException e) {
                setState(State.STOPPED_WITH_ERROR);
                applicationEventPublisher.publishEvent(new AutomatStateEvent(state, currentCommand, e));
            }

        } else {
            setState(State.STOPPED_WITH_ERROR);
            applicationEventPublisher.publishEvent(new AutomatStateEvent(state, currentCommand, new UnsupportedOperationException("Unknown Command")));
        }
    }

    @EventListener
    public void onMovementStopped(MovementStoppedEvent event) {
        next();
    }

    @EventListener
    public void onShotDone(ShotDoneEvent event) {
        next();
    }

    private void onTimer() {
        next();
    }

    private RawPosition translatePos(Position position) {
        return new RawPosition(
                (int) translator.degToSteps(position.getX()),
                (int) translator.degToSteps(position.getY()));
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
