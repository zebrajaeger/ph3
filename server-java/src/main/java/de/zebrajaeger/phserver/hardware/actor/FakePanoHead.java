package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.ActorAxisStatus;
import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.hardware.axis.FakeActorAxis;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Profile({"develop"})
public class FakePanoHead extends PollingActor implements Actor {

    @Value("${develop.updatesPerSecond:5}")
    private int updatesPerSecond;

    public int ticsPerSec;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final PanoHeadData panoHeadData = new PanoHeadData();
    private final FakeActorAxis x = new FakeActorAxis();
    private final FakeActorAxis y = new FakeActorAxis();
    private final FakeActorAxis z = new FakeActorAxis();

    public FakePanoHead(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @PostConstruct
    public void init() {
        ticsPerSec = 1000 / updatesPerSecond;
    }

    public void reset() {
        x.reset();
        y.reset();
        z.reset();
    }

    @Override
    public PanoHeadData read() {
        return panoHeadData;
    }

      @Override
    public void startFocus(int focusTimeMs) {
        panoHeadData.getCamera().setFocus(true);
        executorService.schedule(() -> panoHeadData.getCamera().setFocus(false), focusTimeMs,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void startTrigger(int triggerTimeMs) {
        panoHeadData.getCamera().setTrigger(true);
        executorService.schedule(() -> panoHeadData.getCamera().setTrigger(false), triggerTimeMs,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void startShot(int focusTimeMs, int triggerTimeMs) {
        panoHeadData.getCamera().setFocus(true);
        executorService.schedule(() -> {
            panoHeadData.getCamera().setFocus(false);
            panoHeadData.getCamera().setTrigger(true);
            executorService.schedule(() -> panoHeadData.getCamera().setTrigger(false), triggerTimeMs,
                    TimeUnit.MILLISECONDS);
        }, focusTimeMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setLimit(AxisIndex axisIndex, int velocityMinHz, int velocityMaxHz, int accelerationMaxHzPerSecond) {
        getAxis(axisIndex).setLimit(velocityMinHz, velocityMaxHz, accelerationMaxHzPerSecond);
    }

    @Override
    public void setTargetVelocity(AxisIndex axisIndex, int velocity) {
        getAxis(axisIndex).setVelocity(velocity);
    }

    @Override
    public void setTargetPos(AxisIndex axisIndex, int pos) {
        getAxis(axisIndex).setTargetPos(pos);
    }

    @Override
    public void stopAll() {
        x.setVelocity(0);
        y.setVelocity(0);
        z.setVelocity(0);
    }

//    @Override
//    public void setActualPos(AxisIndex axisIndex, int pos) {
//        // TODO
//        throw new UnsupportedOperationException("not implemented");
//    }

    @Override
    public void setActualAndTargetPos(AxisIndex axisIndex, int pos) {
        getAxis(axisIndex).setActualAndTargetPos(pos);
    }

//    @Override
//    public void resetPos() {
//        x.reset();
//        y.reset();
//        z.reset();
//    }

    private FakeActorAxis getAxis(AxisIndex axisIndex) {
        return switch (axisIndex) {
            case X -> x;
            case Y -> y;
            case Z -> z;
            default -> throw new IllegalArgumentException("Wrong axis index: " + axisIndex);
        };
    }

    public void update() {
        x.update(ticsPerSec);
        ActorAxisStatus dataX = panoHeadData.getActorStatus().getX();
        dataX.setMoving(x.isMoving());
        dataX.setAtTargetPos(x.isAtPos());
        dataX.setPos(x.getPos());

        y.update(ticsPerSec);
        ActorAxisStatus dataY = panoHeadData.getActorStatus().getY();
        dataY.setMoving(y.isMoving());
        dataY.setAtTargetPos(y.isAtPos());
        dataY.setPos(y.getPos());

        z.update(ticsPerSec);
        ActorAxisStatus dataZ = panoHeadData.getActorStatus().getZ();
        dataZ.setMoving(z.isMoving());
        dataZ.setAtTargetPos(z.isAtPos());
        dataZ.setPos(z.getPos());
    }
}
