package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.data.ShotPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class MatrixPanoGenerator implements PanoGenerator {

  private double backlashAngle;

  public PanoMatrix calculatePano(Position currentPosDeg, Image image, Pano pano) {
    PanoMatrix result = new PanoMatrix();
    setYPositions(image, pano, result);
    setXPositions(currentPosDeg, image, pano, result);
    return result;
  }


  private ArrayList<Double> equidistant(int count, double startPos, double distance) {
    ArrayList<Double> result = new ArrayList<>();
    for (int i = 0; i < count; ++i) {
      result.add(startPos + (distance * i));
    }
    return result;
  }

  @SuppressWarnings("UnnecessaryLocalVariable")
  private ArrayList<Double> calcVFull(double lImage, double overlap) {
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

  @SuppressWarnings("UnnecessaryLocalVariable")
  private ArrayList<Double> calcHFull(double startPosition, double lImage, double overlap) {
    // f: full image length
    double f = lImage;
    // f: image length
    double o = overlap;

    // lp = 360  // lp:  pano length
    double lp = 360;

    // p = f - (f * o) // p: partial image length
    double p = f - (f * o);

    // n: image count
    // lp = n * p -> n = lp / p
    double n1 = lp / p;
    int n = (int) Math.ceil(n1);

    // d: distance between images
    double d = lp / n;

    return equidistant(n, startPosition, d);
  }

  private void setXPositions(Position currentPosDeg, Image image, Pano pano, PanoMatrix result) {
    double lImage = Math.abs(image.getWidth());

    if (pano.getFieldOfViewPartial().isFullX()) {
      // full range x
      double startPos = currentPosDeg.getX();
      final ArrayList<Double> positions = calcHFull(startPos, lImage,
          pano.getHorizontalMinimumOverlap());
      result.setAllXPositions(positions);

    } else {
      // Partial range y
      double lPano = Math.abs(pano.getFieldOfViewPartial().getHorizontal().getSize());
      double startPos = Math.min(
          pano.getFieldOfViewPartial().getHorizontal().getFrom(),
          pano.getFieldOfViewPartial().getHorizontal().getTo());

      result.setAllXPositions(
          new MatrixCalculatorPartial().calculatePositions(
              startPos,
              lImage,
              lPano,
              pano.getHorizontalMinimumOverlap()));
    }
  }

  private void setYPositions(Image image, Pano pano, PanoMatrix result) {
    double lImage = Math.abs(image.getHeight());

    MatrixCalculator calc;
    if (pano.getFieldOfViewPartial().isFullY()) {
      // Full y range
      result.setYPositions(calcVFull(image.getHeight(), pano.getHorizontalMinimumOverlap()));

    } else {
      // Partial y range
      double lPano;
      double startPos;
      calc = new MatrixCalculatorPartial();
      lPano = Math.abs(pano.getFieldOfViewPartial().getVertical().getSize());
      startPos = Math.min(
          pano.getFieldOfViewPartial().getVertical().getFrom(),
          pano.getFieldOfViewPartial().getVertical().getTo());
      result.setYPositions(
          calc.calculatePositions(startPos, lImage, lPano, pano.getHorizontalMinimumOverlap()));
    }
  }

  @Override
  public List<Command> createCommands(Position currentPosDeg, PanoMatrix panoMatrix,
      List<Shot> shots,
      Delay delay) {

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
      for (double xPosition : panoMatrix.getXPositions(yIndex)) {

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
