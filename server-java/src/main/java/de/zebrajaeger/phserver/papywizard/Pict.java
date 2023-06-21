package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pict {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH\\hmm\\mss\\s");
    @JacksonXmlProperty(isAttribute = true)
    private Integer bracket;
    @JacksonXmlProperty(isAttribute = true)
    private Integer id;
    private String time;
    private Position position;

    public void setTime(LocalDateTime dateTime) {
        time = dateTime.format(DATE_TIME_FORMATTER);
    }
}
