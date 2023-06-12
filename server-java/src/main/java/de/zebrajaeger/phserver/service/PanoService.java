package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.*;
import de.zebrajaeger.phserver.event.*;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.PanoGenerator;
import de.zebrajaeger.phserver.pano.PositionGeneratorSparseSquare;
import de.zebrajaeger.phserver.pano.PositionGeneratorSquare;
import de.zebrajaeger.phserver.papywizard.PapywizardGenerator;
import de.zebrajaeger.phserver.settings.CameraFovSettings;
import de.zebrajaeger.phserver.settings.PanoFovSettings;
import de.zebrajaeger.phserver.settings.PicturePresetsSettings;
import de.zebrajaeger.phserver.settings.Settings;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@Setter
@Slf4j
public class PanoService {

    private static final SimpleDateFormat STRUCTURED_DATE_PATTERN = new SimpleDateFormat(
            "yyyy-MM-dd_hh-mm-ss");
    private final PanoHeadService panoHeadService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SettingsService settingsService;
    private final ShotService shotService;
    private final DelayService delayService;

    private final PanoFovSettings cameraFov = new PanoFovSettings();
    private final PanoFovSettings panoFov = new PanoFovSettings();
    private double minimumOverlapH = 0.25;
    private double minimumOverlapV = 0.25;
    private Pattern pattern;
    private Optional<PanoMatrix> panoMatrix = Optional.empty();
    private PanoGenerator panoGenerator = new PanoGenerator(5d);
    private PicturePresetsSettings picturePresets = new PicturePresetsSettings();

    public PanoService(PanoHeadService panoHeadService,
                       ApplicationEventPublisher applicationEventPublisher,
                       SettingsService settingsService,
                       ShotService shotService,
                       DelayService delayService) {
        this.panoHeadService = panoHeadService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.settingsService = settingsService;
        this.shotService = shotService;
        this.delayService = delayService;
    }

    @PostConstruct
    public void init() {
        final Settings settings = settingsService.getSettingsStore().getSettings();

        settings.getPictureFov().read(cameraFov);
        publishPictureFOVChange();

        settings.getPanoFov().read(panoFov);
        publishPanoFOVChange();

        pattern = settings.getPano().getPattern();
        publishPatternChange();

        settingsService.getPictureFovPresetsStore().getSettings().read(picturePresets);
        publishPicturePresetsChange();
    }

    public void setCurrentPositionAsPictureBorder(Border... borders) {
        setCurrentPositionAsBorder(cameraFov, borders);
    }

    public void setCurrentPositionAsPanoBorder(Border... borders) {
        setCurrentPositionAsBorder(panoFov, borders);
    }

    public void setCurrentPositionAsBorder(PanoFovSettings fov, Border... borders) {
        for (Border b : borders) {
            Position currentPosition = panoHeadService.getCurrentPosition();
            switch (b) {
                case LEFT -> fov.getX().setFrom(currentPosition.getX());
                case RIGHT -> fov.getX().setTo(currentPosition.getX());
                case TOP -> fov.getY().setFrom(currentPosition.getY());
                case BOTTOM -> fov.getY().setTo(currentPosition.getY());
            }
        }
    }

    public Optional<PanoMatrix> updatePanoMatrix() {
        log.info("updateCalculatedPano() - CALL");

        if (cameraFov.isComplete() && panoFov.isComplete()) {

            log.info("updateCalculatedPano() - RECALCULATE");
            final CameraFovSettings camera = new CameraFovSettings();
            camera.write(cameraFov);
            Pano pano = new Pano(panoFov.normalize(), getMinimumOverlapH(), getMinimumOverlapV());

            panoMatrix = Optional.of(switch (pattern) {
                case GRID -> new PositionGeneratorSquare().calculatePano(
                        panoHeadService.getCurrentPositionDeg(),
                        camera,
                        pano);
                case SPARSE -> new PositionGeneratorSparseSquare().calculatePano(
                        panoHeadService.getCurrentPositionDeg(),
                        camera,
                        pano);
            });

            panoMatrix.ifPresent(
                    value -> applicationEventPublisher.publishEvent(new PanoMatrixChangedEvent(value)));
        }
        return panoMatrix;
    }

    public List<Command> createCommands(PanoMatrix panoMatrix) {

        return panoGenerator.createCommands(
                panoHeadService.getCurrentPositionDeg(),
                panoMatrix,
                shotService.getCurrent(),
                delayService.getDelay());
    }

    public void createPapywizardFile(PanoMatrix calculatedPano) {
        final PapywizardGenerator g = new PapywizardGenerator();
        final String xml = g.generate(calculatedPano);
        final Date now = new Date();
        final File file = new File(STRUCTURED_DATE_PATTERN.format(now)
                + "-(" + calculatedPano.getMaxXSize() + "-" + calculatedPano.getMaxY() + ")"
                + "-papywizard.xml");
        log.info("Write papywizard file to: '{}'", file.getAbsolutePath());
        try {
            FileUtils.write(file, xml, Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Could not write papywizard file to: '{}'", file.getAbsolutePath(), e);
        }
    }

    public void publishPictureFOVChange() {
        if (cameraFov.isComplete()) {
            settingsService.getSettingsStore().getSettings().getPictureFov().write(cameraFov);
            settingsService.getSettingsStore().saveDelayed();
        }
        applicationEventPublisher.publishEvent(new PictureFOVChangedEvent(cameraFov));
    }

    public void publishPanoFOVChange() {
        settingsService.getSettingsStore().getSettings().getPanoFov().write(panoFov);
        settingsService.getSettingsStore().saveDelayed();
        applicationEventPublisher.publishEvent(new PanoFOVChangedEvent(panoFov));
    }

    public void publishPatternChange() {
        settingsService.getSettingsStore().getSettings().getPano().setPattern(pattern);
        settingsService.getSettingsStore().saveDelayed();
        applicationEventPublisher.publishEvent(new PatternChangedEvent(pattern));
    }

    public void publishPicturePresetsChange() {
        settingsService.getPictureFovPresetsStore().getSettings().write(picturePresets);
        settingsService.getPictureFovPresetsStore().saveDelayed();
        applicationEventPublisher.publishEvent(new PictureFovNamesChangedEvent(picturePresets.getNames()));
    }
}
