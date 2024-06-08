package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.settings.CameraFovSettings;

import java.util.ArrayList;

public abstract class PositionGenerator {

    /**
     * Distribute images over the entire area.<br>
     * With additional border to overlap the circle seam
     *
     * @param lImage  image vertical length
     * @param overlap overlap factor [0..1]
     * @return image positions  that cover the entire area
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    protected ArrayList<Double> calcVFull(double lImage, double overlap) {
        // f: full image length
        double f = lImage;
        // f: image length
        double o = overlap;

        // p = f - (f * o) // p: partial image length
        double p = f - (f * o);

        // b = f * o // b: image overlap length
        double b = f * o;

        // lp = 180  // lp:  pano length
        double lp = 180;

        // lb: pano length + extra border on top and bottom
        // lb = lp + (2*b) = lp + (2*f*o)
        double lb = lp + (2 * b);
        // lb = f + (n-1) * p -> n = (lb-f+p)/p
        double n1 = (lb - f + p) / p;

        // n: image count
        int n = (int) Math.ceil(n1);

        // d: distance between images
        double d = (lp - (f / 2)) / (n - 1);

        // s: first image start position
        double s = -90 + (f / 2) - b;

        return equidistant(n, s, d);
    }

    protected ArrayList<Double> calcHFull(double startPosition, double lImage, double overlap) {
        return calcHFull(startPosition, lImage, 360d, 360d, overlap);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected ArrayList<Double> calcHFull(
            double startPosition,
            double lImage,
            double lPanoToCalculateDistance,
            double lPanoToDistributeImages,
            double overlap) {

        // f: full image length
        double f = lImage;
        // f: image length
        double o = overlap;

        // lp = 360  // lp:  pano length
        double lp = lPanoToCalculateDistance;

        // p = f - (f * o) // p: partial image length
        double p = f - (f * o);

        // n: image count
        // lp = n * p -> n = lp / p
        double n1 = lp / p;
        int n = (int) Math.ceil(n1);

        // d: distance between images
        double d = lp / n;

        double ratio = lPanoToDistributeImages / lPanoToCalculateDistance;

        return equidistant(n, startPosition, d * ratio);
    }

    protected ArrayList<Double> equidistant(int count, double startPos, double distance) {
        ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            result.add(startPos + (distance * i));
        }
        return result;
    }

    protected void setYPositions(CameraFovSettings camera, Pano pano, PanoMatrix result) {
        double lImage = Math.abs(camera.getY());

        if (pano.getFov().isFullY()) {
            // Full y range
            result.setYPositions(calcVFull(camera.getY(), pano.getHorizontalMinimumOverlap()));

        } else {
            // Partial y range
            double lPano;
            double startPos;
            lPano = Math.abs(pano.getFov().getY().getSize());
            startPos = Math.min(
                    pano.getFov().getY().getFrom(),
                    pano.getFov().getY().getTo());
            MatrixCalculatorPartial calc = new MatrixCalculatorPartial();
            result.setYPositions(
                    calc.calculatePositions(startPos, lImage, lPano, pano.getHorizontalMinimumOverlap()));
        }
    }
}
