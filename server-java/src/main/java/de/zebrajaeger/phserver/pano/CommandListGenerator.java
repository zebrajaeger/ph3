package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.math3.util.Precision;

import java.util.LinkedList;
import java.util.List;

public class CommandListGenerator {

    private final Position currentPosDeg;
    private final Image image;
    private final Pano pano;
    private final Shots shots;
    private final Delay delay;

    public CommandListGenerator(Position currentPosDeg, Image image, Pano pano, Shots shots, Delay delay) {
        this.currentPosDeg = currentPosDeg;
        this.image = image;
        this.pano = pano;
        this.shots = shots;
        this.delay = delay;
    }

    public List<Command> generate(String shotName) {
        List<Shot> shotList = this.shots.get(shotName);
        if (shotList == null) {
            throw new IllegalArgumentException(String.format("No shots with name '%s' available", shotName));
        }
        CalculatedPano calculatedPano = calculateMissingValues(currentPosDeg, pano, image);
        return createCommands(calculatedPano, shotList, delay);
    }

    public static CalculatedPano calculateMissingValues(Position currentPosDeg, Pano pano, Image image) {
        CalculatedPano result = new CalculatedPano(pano);

        Calc calc = new Calc();

        // Horizontal
        calc.reset();
        calc.setSourceSize(image.getWidth());
        calc.setOverlap(pano.getHorizontalMinimumOverlap());
        FieldOfViewPartial fov = pano.getFieldOfViewPartial();
        if (fov.isPartial()) {
            calc.setPartial(true);
            Double hSize = fov.getHorizontal().getSize();
            if (hSize == null) {
                throw new IllegalArgumentException(String.format("Pano FOV error: '%s'", fov));
            }
            calc.setTargetSize(hSize);
            calc.setTargetStartPoint(fov.getHorizontal().getFrom());
        } else {
            calc.setPartial(false);
            calc.setTargetSize(360d);
            calc.setTargetStartPoint(currentPosDeg.getX());
        }
        Calc.Result hResult = calc.calc();

        result.setHorizontalPositions(hResult.getStartPositions());
        result.setHorizontalOverlap(hResult.getOverlap());

        // Vertical
        calc.reset();
        calc.setSourceSize(image.getHeight());
        // Always partial
        calc.setPartial(true);
        Double vSize = fov.getVertical().getSize();
        if (vSize == null) {
            throw new IllegalArgumentException(String.format("Pano FOV error: '%s'", fov));
        }
        calc.setTargetSize(vSize);
        calc.setTargetStartPoint(fov.getVertical().getFrom());
        calc.setOverlap(pano.getVerticalMinimumOverlap());
        Calc.Result vResult = calc.calc();

        result.setVerticalPositions(vResult.getStartPositions());
        result.setVerticalOverlap(vResult.getOverlap());

        return result;
    }

    private GoToPosCommand createInitialGoTo(CalculatedPano pano) {
        // first command: move 5° left
        double x1 = currentPosDeg.getX();
        double x2 = pano.getHorizontalPositions().get(0) - 5;
        double y = pano.getVerticalPositions().get(0);
        if (Precision.equals(x1, x2, 1d)) {
            // to close together start extra 2° left (=7°)
            x2 -= 2d;
        }
        Position position = new Position(x2, y);
        return new GoToPosCommand(String.format("GoToIdx: %d,%d", -1, 0), position);
    }

    public List<Command> createCommands(CalculatedPano pano, List<Shot> shots, Delay delay) {
        List<Command> commands = new LinkedList<>();

        commands.add(createInitialGoTo(pano));

        // rows
        int rowIndex = 0;
        for (double col : pano.getVerticalPositions()) {

            // columns
            int colIndex = 0;
            for (double row : pano.getHorizontalPositions()) {

                Position position = new Position(row, col);

                commands.add(new GoToPosCommand(String.format("GoToIdx: %d,%d", colIndex, rowIndex), position));
                if (delay.getWaitAfterMove() > 0) {
                    commands.add(new WaitCommand("WaitAfterMove", delay.getWaitAfterMove()));
                }

                // shots
                for (int i = 0; i < shots.size(); ++i) {
                    Shot shot = shots.get(i);
                    commands.add(new TakeShotCommand(String.format("Shot: %d of %d", (i + 1), shots.size()), shot));

                    if (i < shots.size() && delay.getWaitBetweenShots() > 0) {
                        commands.add(new WaitCommand("WaitBetweenShots", delay.getWaitBetweenShots()));
                    }
                }

                if (delay.getWaitAfterShot() > 0) {
                    commands.add(new WaitCommand("WaitAfterShot", delay.getWaitAfterShot()));
                }
                colIndex++;
            }
            rowIndex++;
        }
        return commands;
    }

    public Image getImage() {
        return image;
    }

    public Pano getPano() {
        return pano;
    }

    public Shots getShots() {
        return shots;
    }

    public Delay getDelaySettings() {
        return delay;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
