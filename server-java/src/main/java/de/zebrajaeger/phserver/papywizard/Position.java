package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {

  // x
  @JacksonXmlProperty(isAttribute = true)
  private double yaw;
  // y
  @JacksonXmlProperty(isAttribute = true)
  private double pitch;
}
