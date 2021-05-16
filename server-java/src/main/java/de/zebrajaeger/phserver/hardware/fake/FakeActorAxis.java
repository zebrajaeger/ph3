package de.zebrajaeger.phserver.hardware.fake;

class FakeActorAxis {
    private int maxSpeed;
    private float currentPos;
    private int targetPos;
    private float velocity;
    private boolean velocityMode;
    private boolean atPos;
    private boolean isMoving;

    public FakeActorAxis() {
        reset();
    }

    public void reset() {
        maxSpeed = 1000;
        currentPos = 0f;
        targetPos = 0;
        velocity = 0f;
        velocityMode = false;
        atPos = true;
        isMoving = false;
    }

    public void setLimit(int limit) {
        this.maxSpeed = limit;
    }

    public void setTargetPos(int pos) {
        velocityMode = false;
        targetPos = pos;
    }

    public void setVelocity(int velocity) {
        velocityMode = true;
        this.velocity = velocity;
    }

    public void update(int ticsPerSec) {
        if (velocityMode) {
            currentPos += (velocity / (float) ticsPerSec);
            atPos = false;
            isMoving = true;
        } else {
            float speed = maxSpeed / (float) ticsPerSec;
            float diff = targetPos - currentPos;
            float idiff = Math.round(targetPos) - Math.round(currentPos);
            if (idiff != 0) {
                isMoving = true;
                atPos = false;
                if (diff > 0) {
                    currentPos += Math.min(diff, speed);
                } else {
                    currentPos += Math.max(diff, -speed);
                }
            } else {
                isMoving = false;
                atPos = true;
            }
        }
    }

    public boolean isAtPos() {
        return atPos;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public int getPos() {
        return Math.round(currentPos);
    }
}
