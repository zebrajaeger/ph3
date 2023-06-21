package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.PanoMatrixPosition;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.ShotPosition;
import de.zebrajaeger.phserver.settings.DelaySettings;
import de.zebrajaeger.phserver.settings.ShotSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class PanoGenerator {

    private double backlashAngle;

    public PanoGenerator(double backlashAngle) {
        this.backlashAngle = backlashAngle;
    }

    public List<Command> createCommands(Position currentPosDeg, PanoMatrix panoMatrix,
                                        List<ShotSettings> shots,
                                        DelaySettings delay) {

        log.info("<Create Commands>");
        log.info("*  panoMatrix: '{}'", panoMatrix);
        log.info("*  shots: '{}'", shots);
        log.info("*  delay: '{}'", delay);
        log.info("</Create Commands>");

        List<Command> commands = new LinkedList<>();

        int posIndex = 0;
        ShotPosition lastShotPosition = new ShotPosition(
                currentPosDeg.getX(),
                currentPosDeg.getY(),
                -1, -1, -1, -1, -1);

        boolean first = true;

        // rows
        int yIndex = 0;
        int yLength = panoMatrix.getYPositions().size();
        List<Double> yPositions = new ArrayList<>(panoMatrix.getYPositions());
        Collections.reverse(yPositions);

        double xOffset = 0;
        for (double yPosition : yPositions) {

            // columns
            int xIndex = 0;
            int xLength = panoMatrix.getXPositions(yIndex).size();
            for (PanoMatrixPosition xPosition : panoMatrix.getXPositions(yIndex)) {

                if (xIndex == 0) {
                    double delta = (xPosition.getX() + xOffset) - lastShotPosition.getX();

                    // more than 1/2 revolution backwards?
                    if (delta < -180d) {
                        xOffset += 360;
                    }

                    // more than 1/2 revolution forward?
                    if (delta > 180d) {
                        xOffset -= 360;
                    }
                }

                double x = xPosition.getX() + xOffset;
                if (first) {
                    // this is the first position, so we go a little left and up
                    // before we go to the target position. This is to avoid the backlash
                    ShotPosition antiBackLashPos = new ShotPosition(
                            x - backlashAngle, yPosition - backlashAngle,
                            posIndex,
                            xIndex, xLength,
                            yIndex, yLength
                    );
                    commands.add(new GoToPosCommand(antiBackLashPos, "XY anti-backlash move"));
                    first = false;
                } else {
                    // if we go left, we go a little more left before, we go to the target position,
                    // so we avoid the backlash
                    if (x - lastShotPosition.getX() < 0) {
                        ShotPosition antiBackLashPos = new ShotPosition(
                                x - backlashAngle, yPosition,
                                posIndex,
                                xIndex, xLength,
                                yIndex, yLength
                        );
                        commands.add(new GoToPosCommand(antiBackLashPos, "X anti-backlash move"));
                    }
                }

                ShotPosition shotPos = new ShotPosition(
                        x, yPosition,
                        posIndex,
                        xIndex, xLength,
                        yIndex, yLength
                );

                commands.addAll(createCommandsForPos(
                        shotPos,
                        xIndex, yIndex,
                        shots,
                        delay));

                commands.addAll(createApplyOffsetIfNeeded(shotPos));

                posIndex++;
                xIndex++;
                lastShotPosition = shotPos;
            }
            yIndex++;
        }

        int lastYIndex = panoMatrix.getYSize() - 1;
        commands.add(new NormalizePositionCommand(
                new ShotPosition(
                        0, 0,
                        posIndex,
                        panoMatrix.getXSize(lastYIndex) - 1,
                        panoMatrix.getXSize(lastYIndex),
                        panoMatrix.getYSize() - 1,
                        panoMatrix.getYSize()),
                "NormalizePosition"));

        return commands;
    }

    private List<Command> createCommandsForPos(
            ShotPosition shotPos,
            int xIndex, int yIndex, List<ShotSettings> shots,
            DelaySettings delay) {

        List<Command> commands = new ArrayList<>();

        // now go to shot position
        commands.add(new GoToPosCommand(shotPos, String.format("Go to: (%d,%d)", xIndex, yIndex)));

        // wait until the swinging is over
        if (delay.getWaitAfterMove() > 0) {
            commands.add(new WaitCommand(shotPos, "Wait after move", delay.getWaitAfterMove()));
        }

        // shots
        for (int i = 0; i < shots.size(); ++i) {
            ShotSettings shot = shots.get(i);

            // shot itself
            commands.add(new TakeShotCommand(
                    shotPos,
                    String.format("Shot: %d of %d", (i + 1), shots.size()), shot));

            // if there is another shot, wait if needed
            if (i < shots.size() && delay.getWaitBetweenShots() > 0) {
                commands.add(
                        new WaitCommand(shotPos, "Wait between shots", delay.getWaitBetweenShots()));
            }
        }

        // if needed, wait after the last shot
        if (delay.getWaitAfterShot() > 0) {
            commands.add(new WaitCommand(shotPos, "Wait after shot", delay.getWaitAfterShot()));
        }

        commands.add(new ApplyOffsetCommand(shotPos, "Apply offset"));
        return commands;
    }

    private List<Command> createApplyOffsetIfNeeded(ShotPosition shotPos) {
        if (shotPos.getXIndex() == shotPos.getXLength() - 1) {
            return List.of(new ApplyOffsetCommand(shotPos, "Apply axis offset"));
        }
        return Collections.emptyList();
    }
}
