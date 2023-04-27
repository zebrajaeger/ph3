package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.data.ShotPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@Slf4j
public class MatrixPanoGenerator implements PanoGenerator {

  private double backlashAngle;
  private boolean spreadPano;

  public CalculatedPano calculatePano(Position currentPosDeg, Image image, Pano pano) {

    CalculatedPano result = new CalculatedPano(pano);
    result.setStartPosition(currentPosDeg);

    {
      // x
      double lImage = Math.abs(image.getWidth());

      double lPano;
      double leftBorder;
      MatrixCalculator calc;
      if (pano.getFieldOfViewPartial().isPartial()) {
        calc = new MatrixCalculatorPartial(spreadPano);
        lPano = Math.abs(pano.getFieldOfViewPartial().getHorizontal().getSize());
        leftBorder = Math.min(
            pano.getFieldOfViewPartial().getHorizontal().getFrom(),
            pano.getFieldOfViewPartial().getHorizontal().getTo());
      } else {
        calc = new MatrixCalculator360();
        lPano = 360d;
        leftBorder = currentPosDeg.getX();
      }

      result.setHorizontalPositions(
          calc.calculatePositions(leftBorder, lImage, lPano, pano.getHorizontalMinimumOverlap()));
    }

    {
      // y
      double topBorder = Math.min(
          pano.getFieldOfViewPartial().getVertical().getFrom(),
          pano.getFieldOfViewPartial().getVertical().getTo());
      double lImage = Math.abs(image.getHeight());
      double lPano = Math.abs(pano.getFieldOfViewPartial().getVertical().getSize());
      MatrixCalculator calc = new MatrixCalculatorPartial(spreadPano);

      //noinspection SuspiciousNameCombination
      result.setVerticalPositions(
          calc.calculatePositions(topBorder, lImage, lPano, pano.getHorizontalMinimumOverlap()));
    }

    return result;
  }

  @Override
  public Positions createPositions(CalculatedPano calculatedPano) {

    List<Double> xPositions = new ArrayList<>(calculatedPano.getHorizontalPositions());
    List<Double> yPositions = new ArrayList<>(calculatedPano.getVerticalPositions());
    Collections.reverse(yPositions);
    Positions result = new Positions(xPositions.size(), yPositions.size());

    int index = 0;
    int yIndex = 0;
    for (double yPosition : yPositions) {
      int xIndex = 0;
      for (double xPosition : xPositions) {
        new ShotPosition(xPosition, yPosition, index, xIndex, xPositions.size(), yIndex,
            yPositions.size());
        xIndex++;
        index++;
      }
      yIndex++;
    }
    return result;
  }

  @Override
  public List<Command> createCommands(CalculatedPano calculatedPano, List<Shot> shots,
      Delay delay) {

    log.info("<Create Commands>");
    log.info("*  calculatedPano: '{}'", calculatedPano);
    log.info("*  shots: '{}'", shots);
    log.info("*  delay: '{}'", delay);
    log.info("</Create Commands>");

    List<Command> commands = new LinkedList<>();

    int posIndex = 0;
    ShotPosition lastShotPosition = new ShotPosition(
        calculatedPano.getStartPosition().getX(),
        calculatedPano.getStartPosition().getY(),
        -1, -1, -1, -1, -1);

    boolean first = true;

    // rows
    int yIndex = 0;
    int yLength = calculatedPano.getVerticalPositions().size();
    List<Double> yPositions = new ArrayList<>(calculatedPano.getVerticalPositions());
    Collections.reverse(yPositions);

    double xOffset = 0;
    for (double yPosition : yPositions) {

      // columns
      int xIndex = 0;
      int xLength = calculatedPano.getHorizontalPositions().size();
      for (double xPosition : calculatedPano.getHorizontalPositions()) {

        if (xIndex == 0) {
          double delta = (xPosition + xOffset) - lastShotPosition.getX();

          // more than 1/2 revolution backwards?
          if (delta < -180d) {
            xOffset += 360;
          }

          // more than 1/2 revolution forward?
          if (delta > 180d) {
            xOffset -= 360;
          }
        }

        double x = xPosition + xOffset;
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
