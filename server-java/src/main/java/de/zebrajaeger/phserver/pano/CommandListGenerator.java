package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.DelaySettings;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Shot;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.LinkedList;
import java.util.List;

public class CommandListGenerator {

    private final Image image;
    private final Pano pano;
    private final List<Shot> shots;
    private final DelaySettings delaySettings;

    public CommandListGenerator(Image image, Pano pano, List<Shot> shots, DelaySettings delaySettings) {
        this.image = image;
        this.pano = pano;
        this.shots = shots;
        this.delaySettings = delaySettings;
    }

    public List<Command> generate() {
        CalculatedPano calculatedPano = calculateMissingValues(pano, image);
        return createCommands(calculatedPano, shots, delaySettings);
    }

    public CalculatedPano calculateMissingValues(Pano pano, Image image) {

        CalculatedPano result = new CalculatedPano(pano);

        Calc calc = new Calc();

        calc.reset();
        calc.setSourceSize(image.getWidth());
        if (pano.getFieldOfView().getHorizontal() != null) {
            calc.setPartial(true);
            calc.setTargetSize(pano.getFieldOfView().getHorizontal().getSize());
            calc.setTargetStartPoint(pano.getFieldOfView().getHorizontal().getFrom());
        }
        calc.setOverlap(pano.getHorizontalMinimumOverlap());
        Calc.Result hResult = calc.calc();

        result.setHorizontalPositions(hResult.getStartPositions());
        result.setHorizontalOverlap(hResult.getOverlap());

        calc.reset();
        calc.setSourceSize(image.getHeight());
        if (pano.getFieldOfView().getVertical() != null) {
            calc.setPartial(true);
            calc.setTargetSize(pano.getFieldOfView().getVertical().getSize());
            calc.setTargetStartPoint(pano.getFieldOfView().getVertical().getFrom());
        }
        calc.setOverlap(pano.getVerticalMinimumOverlap());
        Calc.Result vResult = calc.calc();

        result.setVerticalPositions(vResult.getStartPositions());
        result.setVerticalOverlap(vResult.getOverlap());

        return result;
    }

    public List<Command> createCommands(CalculatedPano pano, List<Shot> shots, DelaySettings delaySettings) {
        List<Command> commands = new LinkedList<>();
        int index = 0;

        // rows
        int rowIndex = 0;
        for (double col : pano.getVerticalPositions()) {

            // columns
            int colIndex = 0;
            for (double row : pano.getHorizontalPositions()) {

                Position position = new Position(row, col);

                commands.add(new GoToPosCommand(index++, String.format("GoTo: %d,%d", colIndex, rowIndex), position));
                if (delaySettings.getWaitAfterMove() > 0) {
                    commands.add(new WaitCommand(index++, "WaitAfterMove", delaySettings.getWaitAfterMove()));
                }

                // shots
                for (int i = 0; i < shots.size(); ++i) {
                    Shot shot = shots.get(i);
                    commands.add(new TakeShotCommand(index++, String.format("Shot: %d of %d", (i + 1), shots.size()), shot));

                    if (i < shots.size() && delaySettings.getWaitBetweenShots() > 0) {
                        commands.add(new WaitCommand(index++, "WaitBetweenShots", delaySettings.getWaitBetweenShots()));
                    }
                }

                if (delaySettings.getWaitAfterShot() > 0) {
                    commands.add(new WaitCommand(index++, "WaitAfterShot", delaySettings.getWaitAfterShot()));
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

    public List<Shot> getShots() {
        return shots;
    }

    public DelaySettings getDelaySettings() {
        return delaySettings;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
