package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lens {
    @JacksonXmlProperty(isAttribute = true)
    private String type = "rectilinear";
    private Double focal = 24d;
}
