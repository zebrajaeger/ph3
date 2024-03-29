package de.zebrajaeger.phserver.hardware.fake;

import de.zebrajaeger.phserver.data.ActorAxis;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.hardware.PanoHead;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FakePanoHead implements PanoHead {

  public final int ticsPerSec;

  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private final PanoHeadData panoHeadData = new PanoHeadData();
  private final FakeActorAxis x = new FakeActorAxis();
  private final FakeActorAxis y = new FakeActorAxis();
  private final FakeActorAxis z = new FakeActorAxis();

  public FakePanoHead(int ticsPerSec) {
    this.ticsPerSec = ticsPerSec;
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
  public void setLimit(int axisIndex, int velocityMinHz, int velocityMaxHz,
      int accelerationMaxHzPerSecond) {
    if (axisIndex == 0) {
      x.setLimit(velocityMinHz, velocityMaxHz, accelerationMaxHzPerSecond);
    } else if (axisIndex == 1) {
      y.setLimit(velocityMinHz, velocityMaxHz, accelerationMaxHzPerSecond);
    } else if (axisIndex == 2) {
      z.setLimit(velocityMinHz, velocityMaxHz, accelerationMaxHzPerSecond);
    } else {
      throw new IllegalArgumentException("Wrong axis index: " + axisIndex);
    }
  }

  @Override
  public void setTargetVelocity(int axisIndex, int velocity) {
    if (axisIndex == 0) {
      x.setVelocity(velocity);
    } else if (axisIndex == 1) {
      y.setVelocity(velocity);
    } else if (axisIndex == 2) {
      z.setVelocity(velocity);
    } else {
      throw new IllegalArgumentException("Wrong axis index: " + axisIndex);
    }
  }

  @Override
  public void setTargetPos(int axisIndex, int pos) {
    if (axisIndex == 0) {
      x.setTargetPos(pos);
    } else if (axisIndex == 1) {
      y.setTargetPos(pos);
    } else if (axisIndex == 2) {
      z.setTargetPos(pos);
    } else {
      throw new IllegalArgumentException("Wrong axis index: " + axisIndex);
    }
  }

  @Override
  public void stopAll() {
    x.setVelocity(0);
    y.setVelocity(0);
    z.setVelocity(0);
  }

  @Override
  public void setActualPos(int axisIndex, int pos) {
    // TODO
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void setActualAndTargetPos(int axisIndex, int pos) {
    if (axisIndex == 0) {
      x.setActualAndTargetPos(pos);
    } else if (axisIndex == 1) {
      y.setActualAndTargetPos(pos);
    } else if (axisIndex == 2) {
      z.setActualAndTargetPos(pos);
    } else {
      throw new IllegalArgumentException("Wrong axis index: " + axisIndex);
    }
  }

  @Override
  public void resetPos() {
    x.reset();
    y.reset();
    z.reset();
  }

  public void update() {
    x.update(ticsPerSec);
    ActorAxis dataX = panoHeadData.getActor().getX();
    dataX.setMoving(x.isMoving());
    dataX.setAtTargetPos(x.isAtPos());
    dataX.setPos(x.getPos());

    y.update(ticsPerSec);
    ActorAxis dataY = panoHeadData.getActor().getY();
    dataY.setMoving(y.isMoving());
    dataY.setAtTargetPos(y.isAtPos());
    dataY.setPos(y.getPos());

    z.update(ticsPerSec);
    ActorAxis dataZ = panoHeadData.getActor().getZ();
    dataZ.setMoving(z.isMoving());
    dataZ.setAtTargetPos(z.isAtPos());
    dataZ.setPos(z.getPos());
  }
}
