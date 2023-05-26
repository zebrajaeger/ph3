package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.settings.*;
import dev.dirs.ProjectDirectories;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Getter
public class SettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsService.class);

    @Value("${settings.save.delay:10}")
    private int autoSaveDelay;

    private SettingStore<Settings> settingsStore;
    private SettingStore<PicturePresetsSettings> pictureFovPresetsStore;

    @PostConstruct
    public void init() {
        ProjectDirectories myProjDirs = ProjectDirectories.from("de", "zebrajaeger", "panohead");

        final File settingsFile = new File(myProjDirs.configDir, "settings.json");
        settingsStore = new SettingStore<>(settingsFile, new Settings(), autoSaveDelay).load();
        ShotsSettings shots = settingsStore.getSettings().getShots();
        if (!shots.isDefaultShotOk()) {
            LOG.info("Add/replace default shot to loaded settings");
            shots.setDefaultShot(new ShotSettings());
            settingsStore.saveDelayed();
        }

        pictureFovPresetsStore = new SettingStore<>(
                new File(myProjDirs.configDir, "picture-fov-presets.json"),
                new PicturePresetsSettings(), autoSaveDelay).load();
    }

    @PreDestroy
    public void destroy() {
        settingsStore.saveImmediately();
        pictureFovPresetsStore.saveImmediately();
    }

    @Scheduled(fixedRate = 1000)
    public void checkSave() {
        settingsStore.poll1000();
        pictureFovPresetsStore.poll1000();
    }
}
