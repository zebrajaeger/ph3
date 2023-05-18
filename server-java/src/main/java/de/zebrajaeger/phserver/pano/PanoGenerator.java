package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Shot;
import java.util.List;

public interface PanoGenerator {

  PanoMatrix calculatePano(Position currentPosDeg, Image image, Pano pano);

//  Positions createPositions(PanoMatrix calculatedPano);

  List<Command> createCommands(Position currentPosDeg, PanoMatrix calculatedPano, List<Shot> shots,
      Delay delay);
}
