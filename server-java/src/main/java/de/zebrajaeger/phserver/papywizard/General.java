package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class General {
    private String title;
    private String gps;
    private String comment;

    public void setGps(double latitude, double longitude) {
        gps = String.format("%f,%f", latitude, longitude);
    }
}
