package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.data.Shots;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.LinkedList;
import java.util.List;

public class CommandListGenerator {

    private final Image image;
    private final Pano pano;
    private final Shots shots;
    private final Delay delay;

    public CommandListGenerator(Image image, Pano pano, Shots shots, Delay delay) {
        this.image = image;
        this.pano = pano;
        this.shots = shots;
        this.delay = delay;
    }

    public List<Command> generate() {
        CalculatedPano calculatedPano = calculateMissingValues(pano, image);
        return createCommands(calculatedPano, shots, delay);
    }

    public static CalculatedPano calculateMissingValues(Pano pano, Image image) {
        CalculatedPano result = new CalculatedPano(pano);

        Calc calc = new Calc();

        calc.reset();
        calc.setSourceSize(image.getWidth());
        FieldOfViewPartial fov = pano.getFieldOfViewPartial();
        if (fov.getHorizontal() != null) {
            calc.setPartial(fov.isPartial());
            Double hSize = fov.getHorizontal().getSize();
            if (hSize == null) {
                throw new IllegalArgumentException(String.format("Pano FOV error: '%s'", fov));
            }
            calc.setTargetSize(hSize);
            calc.setTargetStartPoint(fov.getHorizontal().getFrom());
        }
        calc.setOverlap(pano.getHorizontalMinimumOverlap());
        Calc.Result hResult = calc.calc();

        result.setHorizontalPositions(hResult.getStartPositions());
        result.setHorizontalOverlap(hResult.getOverlap());

        calc.reset();
        calc.setSourceSize(image.getHeight());
        if (fov.getVertical() != null) {
            calc.setPartial(fov.isPartial());
            Double vSize = fov.getVertical().getSize();
            if (vSize == null) {
                throw new IllegalArgumentException(String.format("Pano FOV error: '%s'", fov));
            }
            calc.setTargetSize(vSize);
            calc.setTargetStartPoint(fov.getVertical().getFrom());
        }
        calc.setOverlap(pano.getVerticalMinimumOverlap());
        Calc.Result vResult = calc.calc();

        result.setVerticalPositions(vResult.getStartPositions());
        result.setVerticalOverlap(vResult.getOverlap());

        return result;
    }

    public List<Command> createCommands(CalculatedPano pano, Shots shots, Delay delay) {
        List<Command> commands = new LinkedList<>();

        // rows
        int rowIndex = 0;
        for (double col : pano.getVerticalPositions()) {

            // columns
            int colIndex = 0;
            for (double row : pano.getHorizontalPositions()) {

                Position position = new Position(row, col);

                commands.add(new GoToPosCommand(String.format("GoTo: %d,%d", colIndex, rowIndex), position));
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
