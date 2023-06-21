package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Shooting {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH\\hmm\\mss\\s");

    private String headOrientation = "up";
    private Orientation cameraOrientation = Orientation.landscape;
    private Double stabilizationDelay = null;
    private Integer counter = null;
    private String startTime = null;
    private String endTime = null;

    public void setStartTime(LocalDateTime dateTime) {
        startTime = toTimeString(dateTime);
    }

    public void setEndTime(LocalDateTime dateTime) {
        endTime = toTimeString(dateTime);
    }

    /**
     * Result: "2014-02-23_13h59m01s"
     */
    private String toTimeString(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
