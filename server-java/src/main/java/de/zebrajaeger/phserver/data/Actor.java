package de.zebrajaeger.phserver.data;

import lombok.Data;

@Data
public class Actor {

  protected ActorAxis x = new ActorAxis();
  protected ActorAxis y = new ActorAxis();
  protected ActorAxis z = new ActorAxis();

  public ActorAxis getByIndex(int index) {
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

  public boolean isActive() {
    return x.isMoving() || y.isMoving() || z.isMoving();
  }
}
