package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.Actor;
import de.zebrajaeger.phserver.data.Border;
import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.DelaySettings;
import de.zebrajaeger.phserver.data.FieldOfView;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.Shot;
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
import java.util.LinkedList;
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
    private List<Shot> shots = new LinkedList<>();
    private DelaySettings delaySettings = new DelaySettings();
    private Optional<CalculatedPano> calculatedPano;

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
        settingsService.save();
    }

    public Optional<CalculatedPano> updateCalculatedPano() {
        FieldOfView cameraFOV = getPictureFOV();
        Optional<Double> height = cameraFOV.getVertical().getSize();
        Optional<Double> width = cameraFOV.getHorizontal().getSize();
        if (height.isEmpty() || width.isEmpty()) {
            throw new IllegalArgumentException(String.format("Camera FOV error: '%s'", cameraFOV));
        }

        Image image = new Image(width.get(), height.get());
        Pano pano = new Pano(getPanoFOV(), getMinimumOverlapH(), getMinimumOverlapV());
        calculatedPano = Optional.of(CommandListGenerator.calculateMissingValues(pano, image));
        calculatedPano.ifPresent(value -> applicationEventPublisher.publishEvent(new CalculatedPanoChangedEvent(value)));

        return calculatedPano;
    }

    public Optional<List<Command>> createCommands() {
        updateCalculatedPano();

        List<Command> result = null;

        if (calculatedPano.isPresent()) {
            CalculatedPano cp = this.calculatedPano.get();
            FieldOfView cameraFOV = getPictureFOV();
            Optional<Double> height = cameraFOV.getVertical().getSize();
            Optional<Double> width = cameraFOV.getHorizontal().getSize();
            Image image = new Image(width.get(), height.get());
            Pano pano = new Pano(getPanoFOV(), getMinimumOverlapH(), getMinimumOverlapV());
            CommandListGenerator generator = new CommandListGenerator(image, pano, getShots(), getDelaySettings());
            result = generator.createCommands(cp, getShots(), getDelaySettings());
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


    public List<Shot> getShots() {
        return shots;
    }

    public void setShots(List<Shot> shots) {
        this.shots = shots;
    }

    public void publishShotsChange() {
        applicationEventPublisher.publishEvent(new ShotsChangedEvent(this.shots));
    }

    public DelaySettings getDelaySettings() {
        return delaySettings;
    }

    public void setDelaySettings(DelaySettings delaySettings) {
        this.delaySettings = delaySettings;
    }

    public void publishDelayChange() {
        applicationEventPublisher.publishEvent(new DelaySettingsChangedEvent(this.delaySettings));
    }

}
