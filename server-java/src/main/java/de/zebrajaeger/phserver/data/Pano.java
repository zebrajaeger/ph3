package de.zebrajaeger.phserver.data;

import de.zebrajaeger.phserver.settings.PanoFovSettings;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Pano {

    private PanoFovSettings fov;
    private double horizontalMinimumOverlap = 0.25d;
    private double verticalMinimumOverlap = 0.25d;

    public Pano(PanoFovSettings fov, double horizontalMinimumOverlap,
                double verticalMinimumOverlap) {
        this.fov = fov;
        this.horizontalMinimumOverlap = horizontalMinimumOverlap;
        this.verticalMinimumOverlap = verticalMinimumOverlap;
    }
}
