package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.AutomateState;
import de.zebrajaeger.phserver.data.PauseState;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.RobotState;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.event.MovementStoppedEvent;
import de.zebrajaeger.phserver.event.RobotStateEvent;
import de.zebrajaeger.phserver.event.ShotDoneEvent;
import de.zebrajaeger.phserver.pano.ApplyOffsetCommand;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.GoToPosCommand;
import de.zebrajaeger.phserver.pano.NormalizePositionCommand;
import de.zebrajaeger.phserver.pano.TakeShotCommand;
import de.zebrajaeger.phserver.pano.WaitCommand;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Getter
public class RobotService {

  private static final Logger LOG = LoggerFactory.getLogger(RobotService.class);

  private final ApplicationEventPublisher applicationEventPublisher;
  private final PanoHeadService panoHeadService;

  private List<Command> commands = new LinkedList<>();
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  private Integer currentCommandIndex = null;
  private final RobotState robotState = new RobotState(AutomateState.STOPPED, PauseState.RUNNING,
      null);

  @Autowired
  public RobotService(PanoHeadService panoHeadService,
      ApplicationEventPublisher applicationEventPublisher) {
    this.panoHeadService = panoHeadService;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void requestStart(List<Command> commands) {
    if (this.robotState.getAutomateState() == AutomateState.STOPPED
        || this.robotState.getAutomateState() == AutomateState.STOPPED_WITH_ERROR) {
      this.commands = commands;
      this.robotState.setCommandCount(commands.size());
      setAutomateState(AutomateState.STARTED).sendUpdate();
      next();
    } else {
      throw new IllegalStateException("Already running");
    }
  }

  public void requestStop() {
    final AutomateState as = robotState.getAutomateState();
    if (as != AutomateState.STOP_REQUEST &&
        as != AutomateState.STOPPING &&
        as != AutomateState.STOPPED) {
      setAutomateState(AutomateState.STOP_REQUEST)
          .setPauseState(PauseState.RUNNING)
          .sendUpdate();
    }
  }

  public void requestPauseOrResume() {
    final AutomateState automateState = this.robotState.getAutomateState();
    if (automateState != AutomateState.STOPPED
        && automateState != AutomateState.STOPPED_WITH_ERROR) {
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

    // STOPPING
    if (this.robotState.getAutomateState() == AutomateState.STOP_REQUEST) {
      onStopRequest();
      return;
    }

    // STOPPING
    if (this.robotState.getAutomateState() == AutomateState.STOPPING) {
      onStopping();
      return;
    }

    // STOPPED
    if (this.robotState.getAutomateState() == AutomateState.STOPPED) {
      onStopped();
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
      executorService.schedule(this::onTimer, ((WaitCommand) currentCommand).getTimeMs(),
          TimeUnit.MILLISECONDS);

    } else {
      if (GoToPosCommand.class.equals(currentCommand.getClass())) {
        setAutomateState(AutomateState.CMD_MOVE).sendUpdate();
        try {
          boolean alreadyAtPosition = panoHeadService.goTo(new Position(
              currentCommand.getShotPosition().getX(),
              currentCommand.getShotPosition().getY()));
          if (alreadyAtPosition) {
            // We need this, because no motion-stop event will occur
            executorService.schedule(this::onTimer, 250, TimeUnit.MILLISECONDS);
          }
        } catch (IOException e) {
          setAutomateState(AutomateState.STOPPED_WITH_ERROR).sendUpdate(e);
        }

      } else if (TakeShotCommand.class.equals(currentCommand.getClass())) {
        setAutomateState(AutomateState.CMD_SHOT).sendUpdate();
        try {
          Shot shot = ((TakeShotCommand) currentCommand).getShot();
          panoHeadService.shot(shot.getFocusTimeMs(), shot.getTriggerTimeMs());
        } catch (IOException e) {
          setAutomateState(AutomateState.STOPPED_WITH_ERROR).sendUpdate(e);
        }

      } else if (ApplyOffsetCommand.class.equals(currentCommand.getClass())) {
        setAutomateState(AutomateState.APPLY_OFFSET).sendUpdate();
          panoHeadService.adaptAxisOffset();
          executorService.schedule(this::onTimer, 250, TimeUnit.MILLISECONDS);

      } else if (NormalizePositionCommand.class.equals(currentCommand.getClass())) {
        setAutomateState(AutomateState.NORMALIZE_POSITION).sendUpdate();
        panoHeadService.normalizeAxisPosition();
        executorService.schedule(this::onTimer, 250, TimeUnit.MILLISECONDS);

      } else {
        setAutomateState(AutomateState.STOPPED_WITH_ERROR).sendUpdate(
            new UnsupportedOperationException("Unknown Command"));
      }
    }
  }

  @EventListener
  public void onMovementStopped(@SuppressWarnings("unused") MovementStoppedEvent event) {
    LOG.debug("onMovementStopped()");
    next();
  }

  @EventListener
  public void onShotDone(@SuppressWarnings("unused") ShotDoneEvent event) {
    LOG.debug("onShotDone()");
    next();
  }

  private void onTimer() {
    LOG.debug("onTimer()");
    next();
  }

  private void onStopRequest() {
    switch (robotState.getAutomateState()) {
      // we have to wait until the end of the move
      case CMD_MOVE -> {
        try {
          panoHeadService.stopAll();
          setAutomateState(AutomateState.STOPPING)
              .setPauseState(PauseState.RUNNING)
              .sendUpdate();
        } catch (IOException e) {
          setAutomateState(AutomateState.STOPPED_WITH_ERROR)
              .setPauseState(PauseState.RUNNING)
              .sendUpdate();
        }
      }

      // we have to wait until the end of the shot
      case CMD_SHOT -> setAutomateState(AutomateState.STOPPING)
          .setPauseState(PauseState.RUNNING)
          .sendUpdate();

      // otherwise we can stop immediately
      default -> {
        setAutomateState(AutomateState.STOPPED)
            .setPauseState(PauseState.RUNNING)
            .sendUpdate();
        triggerNext();
      }
    }
  }

  private void triggerNext() {
    executorService.schedule(this::onTimer, 250, TimeUnit.MILLISECONDS);
  }

  private void onStopping() {
    // TODO trigger timeout
  }

  private void onStopped() {
    panoHeadService.normalizeAxisPosition();
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
}
