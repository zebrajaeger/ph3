package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.settings.CameraFovSettings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@AllArgsConstructor
@Slf4j
public class PositionGeneratorSquare extends PositionGenerator {

    public PanoMatrix calculatePano(Position currentPosDeg, CameraFovSettings camera, Pano pano) {
        PanoMatrix result = new PanoMatrix();
        setYPositions(camera, pano, result);
        setXPositions(currentPosDeg, camera, pano, result);
        result.finalizeData();
        return result;
    }

    private void setXPositions(Position currentPosDeg, CameraFovSettings camera, Pano pano, PanoMatrix result) {
        double lImage = Math.abs(camera.getX());

        if (pano.getFov().isFullX()) {
            // full range x
            double startPos = currentPosDeg.getX();
            final ArrayList<Double> positions = calcHFull(startPos, lImage,
                    pano.getHorizontalMinimumOverlap());
            result.setAllXPositions(positions);

        } else {
            // Partial range y
            double lPano = Math.abs(pano.getFov().getX().getSize());
            double startPos = Math.min(
                    pano.getFov().getX().getFrom(),
                    pano.getFov().getX().getTo());

            result.setAllXPositions(
                    new MatrixCalculatorPartial().calculatePositions(
                            startPos,
                            lImage,
                            lPano,
                            pano.getHorizontalMinimumOverlap()));
        }
    }
}
