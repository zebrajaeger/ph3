package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.settings.CameraFovSettings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class PositionGeneratorSparseSquare extends PositionGenerator {

    public PanoMatrix calculatePano(Position currentPosDeg, CameraFovSettings camera, Pano pano) {
        PanoMatrix result = new PanoMatrix();
        setYPositions(camera, pano, result);
        setXPositions(currentPosDeg, camera, pano, result);
        result.finalizeData();
        return result;
    }

    private double sqrNormalized90(double deg) {
        return (deg * deg) / 8100d; // (deg / 90d) * (deg / 90d)
    }

    private void setXPositions(Position currentPosDeg, CameraFovSettings camera, Pano pano, PanoMatrix result) {
        double lImageX = Math.abs(camera.getX());
        double lImageY = Math.abs(camera.getY());

        int yIndex = 0;
        for (Double y : result.getYPositions()) {
            double y1 = y - lImageY / 2;
            if (y1 > 90) {
                // special case top pole
                y1 = 180 - y1; // 90 - (90-y1);
            }
            if (y1 < -90) {
                // special case bottom pole
                y1 = -180 - y1; // -90 + (-90-y1);
            }
            double y2 = y + lImageY / 2;
            if (y2 > 90) {
                // special case top pole
                y2 = 180 - y2; // 90 - (90-y1);
            }
            if (y1 < -90) {
                // special case bottom pole
                y2 = -180 - y2; // -90 + (-90-y1);
            }

//      final Double lPano = pano.getFieldOfViewPartial().getHorizontal().getSize();

            double lRelativeX;
            if ((y1 > 0 && y2 > 0) || (y1 < 0 && y2 < 0)) {
                double lRelativeX1 = Math.sqrt(1 - sqrNormalized90(y1));
                double lRelativeX2 = Math.sqrt(1 - sqrNormalized90(y2));
                lRelativeX = Math.max(lRelativeX1, lRelativeX2);
            } else {
                // special case: equator
                lRelativeX = 1;
            }

            if (pano.getFov().isFullX()) {
                final ArrayList<Double> xPositions = calcHFull(
                        currentPosDeg.getX(),
                        lImageX,
                        lRelativeX * 360,
                        360,
                        pano.getHorizontalMinimumOverlap());
                result.setXPositions(yIndex, xPositions);

            } else {
                final double lPano = pano.getFov().getX().getSize();
                double lPanoReduced = lRelativeX * lPano;
//        double offset = (lPano - lPanoReduced)/2;
                double offset = 0;
                double startPos = Math.min(
                        pano.getFov().getX().getFrom(),
                        pano.getFov().getX().getTo());
                final List<Double> xPositions = new MatrixCalculatorPartialSparse().calculatePositions(
                        startPos + offset,
                        lImageX,
                        lPanoReduced,
                        lPano,
                        pano.getHorizontalMinimumOverlap());
                result.setXPositions(yIndex, xPositions);
            }
            yIndex++;
        }
    }
}
