package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pict {
  @JacksonXmlProperty(isAttribute = true)
  private int bracket;
  private Position position;
}
