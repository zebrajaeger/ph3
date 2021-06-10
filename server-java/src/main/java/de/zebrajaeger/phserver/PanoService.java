package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.Actor;
import de.zebrajaeger.phserver.data.Border;
import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.FieldOfView;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.Shots;
import de.zebrajaeger.phserver.event.CalculatedPanoChangedEvent;
import de.zebrajaeger.phserver.event.DelaySettingsChangedEvent;
import de.zebrajaeger.phserver.event.OverlapChangedEvent;
import de.zebrajaeger.phserver.event.PanoFOVChangedEvent;
import de.zebrajaeger.phserver.event.PictureFOVChangedEvent;
import de.zebrajaeger.phserver.event.ShotsChangedEvent;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.CommandListGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PanoService {
    private final PanoHeadService panoHeadService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SettingsService settingsService;

    private final Translator translator = new Translator();
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
        settingsService.getSettings().getShots().getAll(shots);
        settingsService.getSettings().getDelay().getAll(delay);
        publishPictureFOVChange();

        // just to set a value for client
        publishPanoFOVChange();
    }

    public void setCurrentPositionAsPictureBorder(Border... borders) throws IOException {
        setCurrentPositionAsBorder(pictureFOV, borders);
    }

    public void setCurrentPositionAsPanoBorder(Border... borders) throws IOException {
        setCurrentPositionAsBorder(panoFOV, borders);
    }

    public void setCurrentPositionAsBorder(FieldOfView fov, Border... borders) throws IOException {
        for (Border b : borders) {
            Actor actor = panoHeadService.getData().getActor();
            switch (b) {
                case TOP:
                    fov.getVertical().setFrom(translator.stepsToDeg(actor.getY().getPos()));
                    break;
                case RIGHT:
                    fov.getHorizontal().setFrom(translator.stepsToDeg(actor.getX().getPos()));
                    break;
                case BOTTOM:
                    fov.getVertical().setTo(translator.stepsToDeg(actor.getY().getPos()));
                    break;
                case LEFT:
                    fov.getHorizontal().setTo(translator.stepsToDeg(actor.getX().getPos()));
                    break;
            }
        }
        settingsService.getSettings().getPictureFov().setAll(fov);
        settingsService.setDirty();
    }

    public Optional<CalculatedPano> updateCalculatedPano() {
        FieldOfView cameraFOV = getPictureFOV();
        Double height = cameraFOV.getVertical().getSize();
        Double width = cameraFOV.getHorizontal().getSize();
        if (height == null || width == null) {
            throw new IllegalArgumentException(String.format("Camera FOV error: '%s'", cameraFOV));
        }

        Image image = new Image(width, height);
        if (getPanoFOV().isComplete()) {
            Pano pano = new Pano(getPanoFOV(), getMinimumOverlapH(), getMinimumOverlapV());
            calculatedPano = Optional.of(CommandListGenerator.calculateMissingValues(pano, image));
            calculatedPano.ifPresent(value -> applicationEventPublisher.publishEvent(new CalculatedPanoChangedEvent(value)));
        }

        return calculatedPano;
    }

    public Optional<List<Command>> createCommands() {
        updateCalculatedPano();

        List<Command> result = null;

        if (calculatedPano.isPresent()) {
            CalculatedPano cp = this.calculatedPano.get();
            FieldOfView cameraFOV = getPictureFOV();
            Double height = cameraFOV.getVertical().getSize();
            Double width = cameraFOV.getHorizontal().getSize();
            Image image = new Image(width, height);
            Pano pano = new Pano(getPanoFOV(), getMinimumOverlapH(), getMinimumOverlapV());
            CommandListGenerator generator = new CommandListGenerator(image, pano, getShots(), getDelay());
            result = generator.createCommands(cp, getShots(), getDelay());
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
        applicationEventPublisher.publishEvent(new PictureFOVChangedEvent(pictureFOV));
    }

    public FieldOfViewPartial getPanoFOV() {
        return panoFOV;
    }

    public void publishPanoFOVChange() {
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
