package de.zebrajaeger.phserver.data;

import lombok.Data;

@Data
public class ActorStatus {

    protected ActorAxisStatus x = new ActorAxisStatus();
    protected ActorAxisStatus y = new ActorAxisStatus();
    protected ActorAxisStatus z = new ActorAxisStatus();

    public ActorAxisStatus getByIndex(int index) {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
        }
        return null;
    }

    public ActorAxisStatus getByIndex(AxisIndex index) {
        switch (index) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
        }
        return null;
    }

    public boolean isActive() {
        return x.isMoving() || y.isMoving() || z.isMoving();
    }
}
