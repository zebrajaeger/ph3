package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.*;
import de.zebrajaeger.phserver.event.*;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.CommandListGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class PanoService {
    private final PanoHeadService panoHeadService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SettingsService settingsService;

    private final FieldOfView pictureFOV = new FieldOfView();
    private final FieldOfViewPartial panoFOV = new FieldOfViewPartial();
    private double minimumOverlapH = 0.25;
    private double minimumOverlapV = 0.25;
    private Shots shots = new Shots();
    private Delay delay = new Delay();
    private Optional<CalculatedPano> calculatedPano = Optional.empty();

    @Autowired
    public PanoService(PanoHeadService panoHeadService,
                       ApplicationEventPublisher applicationEventPublisher,
                       SettingsService settingsService) {
        this.panoHeadService = panoHeadService;
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
                case TOP:
                    fov.getVertical().setFrom(currentPosition.getY());
                    break;
                case RIGHT:
                    fov.getHorizontal().setFrom(currentPosition.getX());
                    break;
                case BOTTOM:
                    fov.getVertical().setTo(currentPosition.getY());
                    break;
                case LEFT:
                    fov.getHorizontal().setTo(currentPosition.getX());
                    break;
            }
        }
    }

    public void updateCalculatedPano() {
        FieldOfView cameraFOV = getPictureFOV();
        Double height = cameraFOV.getVertical().getSize();
        Double width = cameraFOV.getHorizontal().getSize();
        if (height == null || width == null) {
            throw new IllegalArgumentException(String.format("Camera FOV error: '%s'", cameraFOV));
        }

        Image image = new Image(width, height).normalized();
        FieldOfViewPartial panoFOV = getPanoFOV().normalize();
        if (panoFOV.isComplete() && image.isComplete()) {
            System.out.println("Recalculate pano");
            Pano pano = new Pano(panoFOV, getMinimumOverlapH(), getMinimumOverlapV());
            Position currentPosDeg = panoHeadService.getData().getCurrentPosDeg();
            calculatedPano = Optional.of(CommandListGenerator.calculateMissingValues(currentPosDeg, pano, image));
            calculatedPano.ifPresent(value -> applicationEventPublisher.publishEvent(new CalculatedPanoChangedEvent(value)));
        }
    }

    public Optional<List<Command>> createCommands(String shotsName) {
        updateCalculatedPano();

        List<Command> result = null;
        List<Shot> shots = getShots().get(shotsName);

        if (calculatedPano.isPresent() && shots != null && !shots.isEmpty()) {
            CalculatedPano cp = this.calculatedPano.get();
            FieldOfView cameraFOV = getPictureFOV();
            Double height = cameraFOV.getVertical().getSize();
            Double width = cameraFOV.getHorizontal().getSize();
            Image image = new Image(width, height);
            Pano pano = new Pano(getPanoFOV(), getMinimumOverlapH(), getMinimumOverlapV());
            CommandListGenerator generator = new CommandListGenerator(
                    panoHeadService.getCurrentPosition(),
                    image,
                    pano,
                    getShots(),
                    getDelay());
            result = generator.createCommands(cp, shots, getDelay());
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
        applicationEventPublisher.publishEvent(new OverlapChangedEvent(minimumOverlapH, minimumOverlapV));
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
