package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.zebrajaeger.phserver.pano.Positions;
import java.util.List;

public class PapywizardGenerator {

  public String generate(Positions positions) {
    XmlMapper xmlMapper = new XmlMapper();

    final List<Pict> picts = positions
        .getAll(false, true)
        .stream()
        .map(shotPosition -> new Pict(1, new Position(shotPosition.getX(), shotPosition.getY())))
        .toList();

    Shoot shot = new Shoot(picts);
    final Papywizard pw = new Papywizard(shot);

    try {
      return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pw);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to generate papywizard xml content", e);
    }
  }
}
