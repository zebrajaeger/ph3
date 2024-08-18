package de.zebrajaeger.phserver.record;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.event.MovementStoppedEvent;
import de.zebrajaeger.phserver.event.ShotDoneEvent;
import de.zebrajaeger.phserver.pano.*;
import de.zebrajaeger.phserver.papywizard.Papywizard;
import de.zebrajaeger.phserver.service.PanoHeadService;
import de.zebrajaeger.phserver.settings.ShotSettings;
import de.zebrajaeger.phserver.util.PapywizardUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.OnStateMachineError;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@WithStateMachine(id = "RecordSM")
@Slf4j
public class RecordSMService extends StateMachineListenerAdapter<States, Events> {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final PanoHeadService panoHeadService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final StateMachine<States, Events> stateMachine;
    private List<Command> commands;
    private Papywizard papywizard;
    private Command currentCommand;
    private int commandCount;
    private int commandIndex;

    public RecordSMService(PanoHeadService panoHeadService, ApplicationEventPublisher applicationEventPublisher, StateMachine<States, Events> stateMachine) {
        this.panoHeadService = panoHeadService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.stateMachine = stateMachine;
        stateMachine.addStateListener(this);
    }

    public void requestStart(List<Command> commands, Papywizard papywizard) {
        if (stateMachine.getState().getId() == States.STOPPED) {
            this.commands = commands;
            this.papywizard = papywizard;
            this.commandCount = commands.size();
            this.commandIndex = 0;
            sendEvent(Events.START_PAUSE);
        } else {
            log.warn("SM already running");
        }
    }

    public void requestPauseOrResume() {
        sendEvent(Events.START_PAUSE);
    }

    public void requestStop() {
        sendEvent(Events.STOP);
    }

    public RecordState getState() {
        return new RecordState(stateMachine.getState().getIds(), currentCommand, commandIndex, commandCount);
    }

    public void addListener(StateMachineListener<States, Events> l) {
        stateMachine.addStateListener(l);
    }

    @EventListener
    public void onMovementStopped(MovementStoppedEvent ignoredEvent) {
        log.info("MovementStoppedEvent {}", stateMachine.getState().getIds().toString());
        sendEvent(Events.MOVE_DONE);
    }

    @EventListener
    public void onShotDone(ShotDoneEvent ignoredEvent) {
        log.info("ShotDoneEvent {}", stateMachine.getState().getIds().toString());
        sendEvent(Events.SHOT_DONE);
    }

    @OnStateEntry()
    public void onStateEntry() {
        log.info("SM onStateEntry {}", stateMachine.getState().getIds().toString());
        sendUpdate();
    }

    @OnStateMachineError
    public void onError(Exception e) {
        log.error("SM Error {}", stateMachine.getState().getIds().toString(), e);
        sendUpdate(e);
        sendEvent(Events.ERROR);
    }

    @OnStateEntry(target = "STOPPED")
    public void onStopped() {
        log.info("[onStopped] {}", stateMachine.getState().getIds().toString());
    }

    @OnStateEntry(target = "STARTING")
    public void onStarting() {
        log.info("[onStarting] {}", stateMachine.getState().getIds().toString());
        if (papywizard != null) {
            papywizard.getHeader().getShooting().setStartTime(LocalDateTime.now());
        }
        sendEvent(Events.STARTED);
    }

    @OnStateEntry(target = "IDLE")
    public void onIdle() {
        log.info("[onIdle] {}", stateMachine.getState().getIds().toString());
        if (commands.isEmpty()) {
            log.info("    IDLE: no more commands");
            sendEvent(Events.NO_MORE_COMMANDS);
        } else {
            currentCommand = commands.removeFirst();
            commandIndex++;
            log.info("    IDLE: {}", currentCommand.getClass().getName());
            switch (currentCommand) {
                case ApplyOffsetCommand o -> sendEvent(Events.APPLY_OFFSET);
                case GoToPosCommand o -> sendEvent(Events.MOVE);
                case NormalizePositionCommand o -> sendEvent(Events.NORMALIZE_POSITION);
                case TakeShotCommand o -> sendEvent(Events.SHOT);
                case WaitCommand o -> sendEvent(Events.DELAY);
                default -> throw new IllegalStateException("Unexpected value: " + currentCommand);
            }
        }
    }

    @OnStateEntry(target = "EXEC_MOVE")
    public void onMove() throws Exception {
        log.info("[onMove] {}", stateMachine.getState().getIds().toString());
        boolean alreadyAtPosition = panoHeadService.goTo(new Position(
                currentCommand.getShotPosition().getX(),
                currentCommand.getShotPosition().getY()));
        log.info("    alreadyAtPosition: {}", alreadyAtPosition);
        if (alreadyAtPosition) {
            sendEvent(Events.MOVE_DONE);
        }

        // Wait for MovementStoppedEvent
    }

    @OnStateEntry(target = "EXEC_SHOT")
    public void onShot() throws Exception {
        log.info("[onShot] {}", stateMachine.getState().getIds().toString());
        TakeShotCommand tsc = (TakeShotCommand) currentCommand;
        ShotSettings shot = tsc.getShot();
        panoHeadService.shot(shot.getFocusTimeMs(), shot.getTriggerTimeMs());

        if (tsc.getId() != null) {
            // shot time to papywizard
            papywizard.findPictById(tsc.getId()).ifPresent(pict -> pict.setTime(LocalDateTime.now()));
        }

        // Wait for ShotDoneEvent
    }

    @OnStateEntry(target = "EXEC_DELAY")
    public void onDelay() {
        log.info("[onDelay] {}", stateMachine.getState().getIds().toString());
        executorService.schedule(() -> sendEvent(Events.DELAY_DONE),
                ((WaitCommand) currentCommand).getTimeMs(),
                TimeUnit.MILLISECONDS);
    }

    @OnStateEntry(target = "EXEC_APPLY_OFFSET")
    public void onApplyOffset() {
        log.info("[onApplyOffset] {}]", stateMachine.getState().getIds().toString());
//        panoHeadService.adaptAxisOffset();
        sendEvent(Events.APPLY_OFFSET_DONE);
    }

    @OnStateEntry(target = "EXEC_NORMALIZE_POSITION")
    public void onNormalizePosition() {
        log.info("[onNormalizePosition] {}", stateMachine.getState().getIds().toString());
        panoHeadService.normalizeAxisPosition();
        sendEvent(Events.NORMALIZE_POSITION_DONE);
    }

    @OnStateEntry(target = "WRITE_PAPYWIZARD")
    public void onWritePapywizard() {
        log.info("[onWritePapywizard] {}", stateMachine.getState().getIds().toString());
        if (papywizard != null) {
            papywizard.getHeader().getShooting().setEndTime(LocalDateTime.now());
            PapywizardUtils.writePapywizardFile(papywizard, "F");
        }
        sendEvent(Events.SAVE_PAPYWIZARD_DONE);
    }

    @OnStateEntry(target = "STOPPED_SUCCESSFULLY")
    public void onStoppedSuccessfully() {
        log.info("[onStoppedSuccessfully] {}", stateMachine.getState().getIds().toString());
        sendEvent(Events.DONE);
    }

    @OnStateEntry(target = "STOPPED_WITH_ERROR")
    public void onStoppedWithErrors() {
        log.info("[onStoppedWithErrors] {}", stateMachine.getState().getIds().toString());
        sendEvent(Events.DONE);
    }

    private void sendEvent(Events e) {
        log.info("SEND EVENT {}: {} ", stateMachine.getState().getIds().toString(), e);
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(e).build())).subscribe();
    }

    private void sendUpdate() {
        sendUpdate(null);
    }

    private void sendUpdate(@Nullable Exception error) {
        RecordStateEvent event = new RecordStateEvent(getState(), error);
        applicationEventPublisher.publishEvent(event);
    }
}
