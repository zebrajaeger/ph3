package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.data.ShotPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.springframework.lang.Nullable;

public class MatrixPanoGenerator implements PanoGenerator {

  public static final double BACKLASH_ANGLE = 25d;

  @Override
  public List<Command> createCommands(
      Position currentPosDeg,
      Image image,
      Pano pano,
      List<Shot> shots,
      Delay delay) {

    final CalculatedPano calculatedPano = calculatePano(currentPosDeg, image, pano);

    List<Command> commands = new LinkedList<>();

    int posIndex = 0;
    ShotPosition lastShotPosition = null;

    // rows
    int yIndex = 0;
    int yLength = calculatedPano.getVerticalPositions().size();
    for (double yPosition : calculatedPano.getVerticalPositions()) {

      // columns
      int xIndex = 0;
      int xLength = calculatedPano.getHorizontalPositions().size();
      double xOffset = 0;
      for (double xPosition : calculatedPano.getHorizontalPositions()) {

        if (lastShotPosition != null && xIndex == 0) {
          double delta = xPosition - lastShotPosition.getX();
          if (delta < -180d) {
            xOffset += 360;
          }
          if (delta > 180d) {
            // should not happen, right?
            xOffset -= 360;
          }
        }

        final double x = xPosition + xOffset;
        if (lastShotPosition == null) {
          // this is the first position, so we go a little left and up
          // before we go to the target position. This is to avoid the backlash
          ShotPosition antiBackLashPos = new ShotPosition(
              x - BACKLASH_ANGLE, yPosition - BACKLASH_ANGLE,
              posIndex,
              xIndex, xLength,
              yIndex, yLength
          );
          commands.add(new GoToPosCommand(antiBackLashPos, "XY anti-backlash move"));
        } else {
          // if we go left, we go a little more left before, we go to the target position,
          // so we avoid the backlash
          if (x - lastShotPosition.getX() < 0) {
            ShotPosition antiBackLashPos = new ShotPosition(
                x - BACKLASH_ANGLE, yPosition,
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
            shotPos, lastShotPosition,
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

    commands.add(new NormalizePositionCommand(
        new ShotPosition(
            0, 0,
            posIndex,
            calculatedPano.getHorizontalPositions().size() - 1,
            calculatedPano.getHorizontalPositions().size(),
            calculatedPano.getVerticalPositions().size() - 1,
            calculatedPano.getVerticalPositions().size()),
        "NormalizePosition"));

    return commands;
  }

  @Override
  public CalculatedPano calculatePano(Position currentPosDeg, Image image, Pano pano) {
    CalculatedPano result = new CalculatedPano(pano);

    Calc calc = new Calc();

    // ========== Horizontal ==========
    calc.reset();

    calc.setSourceSize(Math.abs(image.getWidth()));
    calc.setOverlap(pano.getHorizontalMinimumOverlap());

    FieldOfViewPartial targetFov = pano.getFieldOfViewPartial();
    if (targetFov.isPartial()) {
      calc.setPartial(true);

      Double hTargetSize = targetFov.getHorizontal().getSize();
      if (hTargetSize == null) {
        throw new IllegalArgumentException(String.format("Pano FOV error: '%s'", targetFov));
      }
      calc.setTargetSize(Math.abs(hTargetSize));

      double xStartPos = Math.min(
          targetFov.getHorizontal().getFrom(),
          targetFov.getHorizontal().getTo());
      calc.setTargetStartPoint(xStartPos);
    } else {
      calc.setPartial(false);
      calc.setTargetSize(360d);
      calc.setTargetStartPoint(currentPosDeg.getX());
    }
    Calc.Result hResult = calc.calc();

    result.setHorizontalPositions(hResult.getStartPositions());
    result.setHorizontalOverlap(hResult.getOverlap());

    // ========== Vertical ==========
    calc.reset();

    calc.setSourceSize(Math.abs(image.getHeight()));

    // Always partial
    calc.setPartial(true);
    Double vTargetSize = targetFov.getVertical().getSize();
    if (vTargetSize == null) {
      throw new IllegalArgumentException(String.format("Pano FOV error: '%s'", targetFov));
    }
    calc.setTargetSize(Math.abs(vTargetSize));
    calc.setTargetStartPoint(targetFov.getVertical().getFrom());
    calc.setOverlap(pano.getVerticalMinimumOverlap());
    Calc.Result vResult = calc.calc();

    result.setVerticalPositions(vResult.getStartPositions());
    result.setVerticalOverlap(vResult.getOverlap());

    return result;
  }

  private List<Command> createCommandsForPos(
      ShotPosition shotPos, @Nullable ShotPosition lastShotPosition,
      int xIndex, int yIndex, List<Shot> shots,
      Delay delay) {

    List<Command> commands = new ArrayList<>();

    // now go to shot position
    commands.add(new GoToPosCommand(shotPos, String.format("Go to: (%d,%d)", xIndex, yIndex)));

    // wait until the swinging is over
    if (delay.getWaitAfterMove() > 0) {
      commands.add(new WaitCommand(shotPos, "Wait after move", delay.getWaitAfterMove()));
    }

    // shots
    for (int i = 0; i < shots.size(); ++i) {
      Shot shot = shots.get(i);

      // shot itself
      commands.add(new TakeShotCommand(
          shotPos,
          String.format("Shot: %d of %d", (i + 1), shots.size()), shot));

      // if there is another shot, wait if needed
      if (i < shots.size() && delay.getWaitBetweenShots() > 0) {
        commands.add(new WaitCommand(shotPos, "Wait between shots", delay.getWaitBetweenShots()));
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
