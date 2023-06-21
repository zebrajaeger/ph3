package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Papywizard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Papywizard {
    @JacksonXmlProperty(isAttribute = true)
    private String version = "c";
    private Header header = new Header();
    private Shoot shoot = new Shoot();

    public String toXml() {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to generate papywizard xml content", e);
        }
    }
}
