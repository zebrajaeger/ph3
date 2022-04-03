package de.zebrajaeger.phserver.data;

import de.zebrajaeger.phserver.StepsToDeg;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PanoHeadData {
    protected Actor actor = new Actor();
    protected Camera camera = new Camera();
    protected int movementRaw;
    protected int cameraRaw;

    public void init() {
        actor.x.setAtTargetPos((movementRaw & 0x01) != 0);
        actor.x.setMoving((movementRaw & 0x02) != 0);
        actor.y.setAtTargetPos((movementRaw & 0x04) != 0);
        actor.y.setMoving((movementRaw & 0x08) != 0);
        camera.focus = ((cameraRaw & 0x01) != 0);
        camera.trigger = ((cameraRaw & 0x02) != 0);
    }

    public Camera getCamera() {
        return camera;
    }

    public Actor getActor() {
        return actor;
    }

    public Position getCurrentPosDeg() {
        return new Position(
                StepsToDeg.INSTANCE.translate(actor.getX().getPos()),
                StepsToDeg.INSTANCE.translate(actor.getY().getPos()));
    }

    public int getMovementRaw() {
        return movementRaw;
    }

    public void setMovementRaw(int movementRaw) {
        this.movementRaw = movementRaw;
    }

    public int getCameraRaw() {
        return cameraRaw;
    }

    public void setCameraRaw(int cameraRaw) {
        this.cameraRaw = cameraRaw;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
