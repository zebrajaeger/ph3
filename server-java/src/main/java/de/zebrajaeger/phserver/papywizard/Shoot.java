package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Shoot {

  @JacksonXmlProperty(localName = "pict")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<Pict> picts = new ArrayList<>();

  public void add(Pict pict) {
    picts.add(pict);
  }
}
