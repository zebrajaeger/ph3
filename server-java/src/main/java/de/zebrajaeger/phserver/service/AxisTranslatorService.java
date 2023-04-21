package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.Position;

public interface AxisTranslatorService {

  double rawToDeg(int rawValue);

  int degToRaw(double degValue);

  default Position fromRaw(int x, int y) {
    return new Position(rawToDeg(x), rawToDeg(y));
  }
}
