package de.zebrajaeger.phserver.data;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GpsBrowserData(Double latitude, Double longitude, Double accuracy,
                             Double altitude, Double altitudeAccuracy,
                             Double heading,
                             Double speed,
                             Long timestamp) {
}
