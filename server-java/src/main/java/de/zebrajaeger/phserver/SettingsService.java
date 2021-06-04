package de.zebrajaeger.phserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.phserver.settings.Settings;
import dev.dirs.ProjectDirectories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

@Service
public class SettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsService.class);

    @Value("${settings.file:#{null}}")
    private File settingsFile;

    private Settings settings = new Settings();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() throws IOException {
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
    public void destroy() throws IOException {
        save();
    }

    public void load() throws IOException {
        if (settingsFile.exists()) {
            settings = mapper.readValue(settingsFile, Settings.class);
        }
    }

    public void save() throws IOException {
        // TODO delayed save after some ms  (1000 or something like that)
        mapper.writerWithDefaultPrettyPrinter().writeValue(settingsFile, settings);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}