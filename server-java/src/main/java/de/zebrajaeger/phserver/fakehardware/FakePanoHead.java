package de.zebrajaeger.phserver.fakehardware;

import de.zebrajaeger.phserver.PanoHead;
import de.zebrajaeger.phserver.data.ActorAxis;
import de.zebrajaeger.phserver.data.PanoHeadData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FakePanoHead implements PanoHead {
    // TODO put to test/aplication.propoerties
    public static final int TICS_PER_SEC = 200;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final PanoHeadData panoHeadData = new PanoHeadData();

    private final FakeActorAxis x = new FakeActorAxis();
    private final FakeActorAxis y = new FakeActorAxis();

    public void reset() {
        x.reset();
        y.reset();
    }

    @Override
    public PanoHeadData read() {
        return panoHeadData;
    }

    @Override
    public void startFocus(int focusTimeMs) {
        panoHeadData.getCamera().setFocus(true);
        executorService.schedule(() -> panoHeadData.getCamera().setFocus(false), focusTimeMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void startTrigger(int triggerTimeMs) {
        panoHeadData.getCamera().setTrigger(true);
        executorService.schedule(() -> panoHeadData.getCamera().setTrigger(false), triggerTimeMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void startShot(int focusTimeMs, int triggerTimeMs) {
        panoHeadData.getCamera().setFocus(true);
        executorService.schedule(() -> {
            panoHeadData.getCamera().setFocus(false);
            panoHeadData.getCamera().setTrigger(true);
            executorService.schedule(() -> panoHeadData.getCamera().setTrigger(false), triggerTimeMs, TimeUnit.MILLISECONDS);
        }, focusTimeMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setLimit(int axis, int limit) {
        if (axis == 0) {
            x.setLimit(limit);
        } else if (axis == 1) {
            y.setLimit(limit);
        } else {
            throw new IllegalArgumentException("Wrong axis index: " + axis);
        }
    }

    @Override
    public void setTargetVelocity(int axis, int velocity) {
        if (axis == 0) {
            x.setVelocity(velocity);
        } else if (axis == 1) {
            y.setVelocity(velocity);
        } else {
            throw new IllegalArgumentException("Wrong axis index: " + axis);
        }
    }

    @Override
    public void setTargetPos(int axis, int pos) {
        if (axis == 0) {
            x.setTargetPos(pos);
        } else if (axis == 1) {
            y.setTargetPos(pos);
        } else {
            throw new IllegalArgumentException("Wrong axis index: " + axis);
        }
    }

    public void update() {
        x.update(TICS_PER_SEC);
        ActorAxis dataX = panoHeadData.getActor().getX();
        dataX.setMoving(x.isMoving());
        dataX.setAtTargetPos(x.isAtPos());
        dataX.setPos(x.getPos());

        y.update(TICS_PER_SEC);
        ActorAxis dataY = panoHeadData.getActor().getY();
        dataY.setMoving(y.isMoving());
        dataY.setAtTargetPos(y.isAtPos());
        dataY.setPos(y.getPos());
    }
}
