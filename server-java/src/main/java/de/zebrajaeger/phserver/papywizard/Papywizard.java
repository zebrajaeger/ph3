package de.zebrajaeger.phserver.papywizard;

import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "Papywizard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Papywizard {
  private Shoot shoot;
}
