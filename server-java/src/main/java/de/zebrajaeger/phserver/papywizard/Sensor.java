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
public class Sensor {
    @JacksonXmlProperty(isAttribute = true)
    private Double coef =1.61;
    @JacksonXmlProperty(isAttribute = true)
    private String ratio="3:2";
}
