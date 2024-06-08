package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preset {
    @JacksonXmlProperty(isAttribute = true)
    private String name = "360";
}
