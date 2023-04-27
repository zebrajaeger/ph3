package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Shot;
import java.util.List;

public interface PanoGenerator {
  CalculatedPano calculatePano(Position currentPosDeg,  Image image, Pano pano);

  Positions createPositions(CalculatedPano calculatedPano);

  List<Command> createCommands(CalculatedPano calculatedPano, List<Shot> shots, Delay delay);
}
