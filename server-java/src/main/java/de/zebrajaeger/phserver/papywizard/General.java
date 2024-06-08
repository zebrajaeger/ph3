package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.zebrajaeger.phserver.data.GpsLocation;
import de.zebrajaeger.phserver.util.GpsUtils;
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

    public void setGps(GpsLocation location) {
        if (location == null) {
            gps = null;
        } else {
            gps = GpsUtils.valueToStringWith1mPrecision(location.latitude())
                    + ","
                    + GpsUtils.valueToStringWith1mPrecision(location.longitude());
        }
    }
}
