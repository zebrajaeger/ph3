package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.zebrajaeger.phserver.util.PapywizardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Shooting {
    private String headOrientation = "up";
    private Orientation cameraOrientation = Orientation.landscape;
    private Double stabilizationDelay = null;
    private Integer counter = null;
    private String startTime = null;
    private String endTime = null;

    public void setStartTime(LocalDateTime dateTime) {
        startTime = PapywizardUtils.toString(dateTime);
    }

    public void setEndTime(LocalDateTime dateTime) {
        endTime = PapywizardUtils.toString(dateTime);
    }
}
