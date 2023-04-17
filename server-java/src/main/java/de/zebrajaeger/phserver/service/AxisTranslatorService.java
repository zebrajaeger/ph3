package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.RawPosition;

public interface AxisTranslatorService {
  double rawToDeg(int rawValue);

  int degToRaw(double degValue);

   default Position  fromRaw(int x, int y) {
    return new Position(rawToDeg(x), rawToDeg(y));
  }

   default RawPosition fromDeg(double x, double y) {
    return new RawPosition(degToRaw(x), degToRaw(y));
  }
}
