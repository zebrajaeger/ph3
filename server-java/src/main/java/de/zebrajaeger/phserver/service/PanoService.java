package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.*;
import de.zebrajaeger.phserver.event.*;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.PanoGenerator;
import de.zebrajaeger.phserver.pano.PositionGeneratorSparseSquare;
import de.zebrajaeger.phserver.pano.PositionGeneratorSquare;
import de.zebrajaeger.phserver.papywizard.PapywizardGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final FieldOfView pictureFOV = new FieldOfView();
    private final FieldOfViewPartial panoFOV = new FieldOfViewPartial();
    private double minimumOverlapH = 0.25;
    private double minimumOverlapV = 0.25;
    private Shots shots = new Shots();
    private Pattern pattern;
    private Delay delay = new Delay();
    private Optional<PanoMatrix> panoMatrix = Optional.empty();
    private PanoGenerator panoGenerator = new PanoGenerator(5d);

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

        pattern = settingsService.getSettings().getPano().getPattern();
        publishPatternChange();
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
                case LEFT -> fov.getHorizontal().setFrom(currentPosition.getX());
                case RIGHT -> fov.getHorizontal().setTo(currentPosition.getX());
                case TOP -> fov.getVertical().setFrom(currentPosition.getY());
                case BOTTOM -> fov.getVertical().setTo(currentPosition.getY());
            }
        }
    }

    public Optional<PanoMatrix> updatePanoMatrix() {
        log.info("updateCalculatedPano() - CALL");
        FieldOfView cameraFOV = getPictureFOV();
        Double height = cameraFOV.getVertical().getSize();
        Double width = cameraFOV.getHorizontal().getSize();
        if (height != null && width != null) {
            Image image = new Image(width, height).normalized();
            FieldOfViewPartial panoFOV = getPanoFOV().normalize();
            if (panoFOV.isComplete() && image.isComplete()) {
                log.info("updateCalculatedPano() - RECALCULATE");
                Pano pano = new Pano(panoFOV, getMinimumOverlapH(), getMinimumOverlapV());

                panoMatrix = Optional.of(switch (pattern) {
                    case GRID -> new PositionGeneratorSquare().calculatePano(
                            panoHeadService.getCurrentPositionDeg(),
                            image,
                            pano);
                    case SPARSE -> new PositionGeneratorSparseSquare().calculatePano(
                            panoHeadService.getCurrentPositionDeg(),
                            image,
                            pano);
                });

                panoMatrix.ifPresent(
                        value -> applicationEventPublisher.publishEvent(new PanoMatrixChangedEvent(value)));
            }
        }
        return panoMatrix;
    }

    public Optional<List<Command>> createCommands(PanoMatrix panoMatrix, String shotsName) {
        List<Shot> shots = getShots().get(shotsName);
        if (shots == null || shots.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(panoGenerator.createCommands(
                panoHeadService.getCurrentPositionDeg(),
                panoMatrix,
                shots, getDelay()));
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
        settingsService.getSettings().getPictureFov().setAll(pictureFOV);
        settingsService.setDirty();
        applicationEventPublisher.publishEvent(new PictureFOVChangedEvent(pictureFOV));
    }

    public void publishPanoFOVChange() {
        settingsService.getSettings().getPanoFov().setAll(panoFOV);
        settingsService.setDirty();
        applicationEventPublisher.publishEvent(new PanoFOVChangedEvent(panoFOV));
    }

    public void publishShotsChange() {
        settingsService.getSettings().getShots().setAll(shots);
        settingsService.setDirty();
        applicationEventPublisher.publishEvent(new ShotsChangedEvent(shots));
    }

    public void publishDelayChange() {
        settingsService.getSettings().getDelay().setAll(delay);
        settingsService.setDirty();
        applicationEventPublisher.publishEvent(new DelaySettingsChangedEvent(delay));
    }

    public void publishPatternChange() {
        settingsService.getSettings().getPano().setPattern(pattern);
        settingsService.setDirty();
        applicationEventPublisher.publishEvent(new PatternChangedEvent(pattern));
    }

}
