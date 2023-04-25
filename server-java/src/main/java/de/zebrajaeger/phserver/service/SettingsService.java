package de.zebrajaeger.phserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.phserver.settings.Settings;
import de.zebrajaeger.phserver.settings.ShotSetting;
import de.zebrajaeger.phserver.settings.ShotsSettings;
import dev.dirs.ProjectDirectories;
import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

  private static final Logger LOG = LoggerFactory.getLogger(SettingsService.class);

  @Value("${settings.file:#{null}}")
  private File settingsFile;

  @Value("${settings.autosave.delay:10}")
  private int autosaveDelay;
  private int autosaveCounter = 0;

  private Settings settings = new Settings();
  private final ObjectMapper mapper = new ObjectMapper();

  @PostConstruct
  public void init() {
    // settings file path
    if (settingsFile == null) {
      ProjectDirectories myProjDirs = ProjectDirectories.from("de", "zebrajaeger", "panohead");
      settingsFile = new File(myProjDirs.configDir, "settings.json");
    }

    // create directory if needed
    File settingsDirectory = settingsFile.getParentFile();
    if (settingsDirectory.mkdirs()) {
      LOG.info("Settings directory created @'{}'", settingsDirectory.getAbsolutePath());
    } else {
      LOG.info("Settings directory already exists: '{}'", settingsDirectory.getAbsolutePath());
    }

    // import data
    load();
  }

  @PreDestroy
  public void destroy() {
    save();
  }

  public void load() {
    if (settingsFile.exists()) {
      LOG.info("Read from settings file: '{}'", settingsFile.getAbsolutePath());
      try {
        settings = mapper.readValue(settingsFile, Settings.class);
        ShotsSettings shots = settings.getShots();
        if (!shots.isDefaultShotOk()) {
          LOG.info("Add/replace default shot to loaded settings");
          shots.setDefaultShot(new ShotSetting());
          setDirty();
        }
      } catch (IOException e) {
        LOG.error("Could not read settings file", e);
      }
    }
  }

  private void save() {
    // TODO delayed save after some ms  (1000 or something like that)
    LOG.info("Save config to '{}'", settingsFile.getAbsolutePath());
    try {
      mapper.writerWithDefaultPrettyPrinter().writeValue(settingsFile, settings);
    } catch (IOException e) {
      LOG.error("Could not save config file", e);
    }
  }

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public void setDirty() {
    autosaveCounter = autosaveDelay;
  }

  @Scheduled(fixedRate = 1000)
  public void checkSave() {
    if (autosaveCounter == 0) {
      save();
      autosaveCounter = -1;
    } else if (autosaveCounter > 0) {
      autosaveCounter--;
    }
  }
}
