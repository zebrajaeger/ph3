package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.Border;
import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.FieldOfView;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.data.Shots;
import de.zebrajaeger.phserver.event.CalculatedPanoChangedEvent;
import de.zebrajaeger.phserver.event.DelaySettingsChangedEvent;
import de.zebrajaeger.phserver.event.OverlapChangedEvent;
import de.zebrajaeger.phserver.event.PanoFOVChangedEvent;
import de.zebrajaeger.phserver.event.PictureFOVChangedEvent;
import de.zebrajaeger.phserver.event.ShotsChangedEvent;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.DefaultPanoGenerator;
import de.zebrajaeger.phserver.pano.PanoGenerator;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
// TODO @Getter @Setter
@Service
public class PanoService {

  private final PanoHeadService panoHeadService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final SettingsService settingsService;
  private final AxisTranslatorServiceImpl axisTranslatorService;

  private final PanoGenerator panoGenerator = new DefaultPanoGenerator();
  private final FieldOfView pictureFOV = new FieldOfView();
  private final FieldOfViewPartial panoFOV = new FieldOfViewPartial();
  private double minimumOverlapH = 0.25;
  private double minimumOverlapV = 0.25;
  private Shots shots = new Shots();
  private Delay delay = new Delay();
  private Optional<CalculatedPano> calculatedPano = Optional.empty();

  @Autowired
  public PanoService(PanoHeadService panoHeadService,
      AxisTranslatorServiceImpl axisTranslatorService,
      ApplicationEventPublisher applicationEventPublisher,
      SettingsService settingsService) {
    this.panoHeadService = panoHeadService;
    this.axisTranslatorService = axisTranslatorService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.settingsService = settingsService;
  }

  @PostConstruct
  public void init() {
    settingsService.getSettings().getPictureFov().getAll(pictureFOV);
    publishPictureFOVChange();

    settingsService.getSettings().getPanoFov().getAll(panoFOV);
    publishPanoFOVChange();

    settingsService.getSettings().getShots().getAll(shots);
    publishShotsChange();

    settingsService.getSettings().getDelay().getAll(delay);
    publishDelayChange();
  }

  public void setCurrentPositionAsPictureBorder(Border... borders) {
    setCurrentPositionAsBorder(pictureFOV, borders);
  }

  public void setCurrentPositionAsPanoBorder(Border... borders) {
    setCurrentPositionAsBorder(panoFOV, borders);
  }

  public void setCurrentPositionAsBorder(FieldOfView fov, Border... borders) {
    for (Border b : borders) {
      Position currentPosition = panoHeadService.getCurrentPosition();
      switch (b) {
        case LEFT:
          fov.getHorizontal().setFrom(currentPosition.getX());
          break;
        case RIGHT:
          fov.getHorizontal().setTo(currentPosition.getX());
          break;
        case TOP:
          fov.getVertical().setFrom(currentPosition.getY());
          break;
        case BOTTOM:
          fov.getVertical().setTo(currentPosition.getY());
          break;
      }
    }
  }

  public void updateCalculatedPano() {
    System.out.println("updateCalculatedPano() - CALL");
    FieldOfView cameraFOV = getPictureFOV();
    Double height = cameraFOV.getVertical().getSize();
    Double width = cameraFOV.getHorizontal().getSize();
    if (height != null && width != null) {
      Image image = new Image(width, height).normalized();
      FieldOfViewPartial panoFOV = getPanoFOV().normalize();
      PanoHeadData data = panoHeadService.getData();
      if (panoFOV.isComplete() && image.isComplete() && data != null) {
        // TODO LOG instead sysout
        System.out.println("updateCalculatedPano() - RECALCULATE");
        Pano pano = new Pano(panoFOV, getMinimumOverlapH(), getMinimumOverlapV());

        Position currentPosDeg = axisTranslatorService.fromRaw(data.getActor().getX().getPos(),data.getActor().getY().getPos());

        calculatedPano = Optional.of(panoGenerator.calculatePano(currentPosDeg, image, pano));
        calculatedPano.ifPresent(
            value -> applicationEventPublisher.publishEvent(new CalculatedPanoChangedEvent(value)));
      }
    }
  }

  public Optional<List<Command>> createCommands(String shotsName) {
    updateCalculatedPano();

    List<Command> result = null;
    List<Shot> shots = getShots().get(shotsName);

    if (calculatedPano.isPresent() && shots != null && !shots.isEmpty()) {
      FieldOfView cameraFOV = getPictureFOV();
      Double height = cameraFOV.getVertical().getSize();
      Double width = cameraFOV.getHorizontal().getSize();
      Image image = new Image(width, height);
      Pano pano = new Pano(getPanoFOV(), getMinimumOverlapH(), getMinimumOverlapV());
      result = panoGenerator.createCommands(
          panoHeadService.getCurrentPosition(),
          image, pano,
          shots, getDelay());
    }
    return Optional.ofNullable(result);
  }

  public double getMinimumOverlapH() {
    return minimumOverlapH;
  }

  public void setMinimumOverlapH(double minimumOverlapH) {
    this.minimumOverlapH = minimumOverlapH;
  }

  public double getMinimumOverlapV() {
    return minimumOverlapV;
  }

  public void setMinimumOverlapV(double minimumOverlapV) {
    this.minimumOverlapV = minimumOverlapV;
  }

  public void publishOverlapChange() {
    applicationEventPublisher.publishEvent(
        new OverlapChangedEvent(minimumOverlapH, minimumOverlapV));
  }

  public FieldOfView getPictureFOV() {
    return pictureFOV;
  }

  public void publishPictureFOVChange() {
    settingsService.getSettings().getPictureFov().setAll(pictureFOV);
    settingsService.setDirty();
    applicationEventPublisher.publishEvent(new PictureFOVChangedEvent(pictureFOV));
  }

  public FieldOfViewPartial getPanoFOV() {
    return panoFOV;
  }

  public void publishPanoFOVChange() {
    settingsService.getSettings().getPanoFov().setAll(panoFOV);
    settingsService.setDirty();
    applicationEventPublisher.publishEvent(new PanoFOVChangedEvent(panoFOV));
  }

  public Shots getShots() {
    return shots;
  }

  public void setShots(Shots shots) {
    this.shots = shots;
  }

  public void publishShotsChange() {
    settingsService.getSettings().getShots().setAll(shots);
    settingsService.setDirty();
    applicationEventPublisher.publishEvent(new ShotsChangedEvent(shots));
  }

  // <editor-fold desc="delay">
  public Delay getDelay() {
    return delay;
  }

  public void setDelay(Delay delay) {
    this.delay = delay;
  }

  public void publishDelayChange() {
    settingsService.getSettings().getDelay().setAll(delay);
    settingsService.setDirty();
    applicationEventPublisher.publishEvent(new DelaySettingsChangedEvent(delay));
  }
  // </editor-fold>

  public Optional<CalculatedPano> getCalculatedPano() {
    return calculatedPano;
  }
}
