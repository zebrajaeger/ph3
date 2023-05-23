package de.zebrajaeger.phserver.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

@Slf4j
@Service
public class FileService {
    private final File root = new File(".");

    public Collection<String> getPapywizardFileNamesOrderedByTimestamp(int maxItems) {
        final File[] files = root.listFiles((dir, name) -> name.endsWith("-papywizard.xml"));
        if (files == null) {
            return Collections.emptyList();
        }

        final TreeMap<Long, File> orderedFiles = new TreeMap<>((a, b) -> -a.compareTo(b));
        for (File f : files) {
            orderedFiles.put(f.lastModified(), f);
        }

        return orderedFiles.values().stream()
                .map(File::getName)
                .limit(maxItems).toList();
    }

    public String readStringFile(String name) {
        try {
            return FileUtils.readFileToString(new File(root, name), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("File '{}' not found", name);
            return null;
        }
    }
}
