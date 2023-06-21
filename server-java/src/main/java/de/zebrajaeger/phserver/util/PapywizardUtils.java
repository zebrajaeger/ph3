package de.zebrajaeger.phserver.util;

import de.zebrajaeger.phserver.papywizard.Papywizard;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class PapywizardUtils {
    private static final SimpleDateFormat STRUCTURED_DATE_PATTERN = new SimpleDateFormat(
            "yyyy-MM-dd_hh-mm-ss");

    public static void writePapywizardFile(Papywizard papywizard) {
        String gps = papywizard.getHeader().getGeneral().getGps();
        final Date now = new Date();
        if (gps == null) {
            gps = "";
        } else {
            gps = "_" + gps;
        }
        final File file = new File(STRUCTURED_DATE_PATTERN.format(now)
                + gps
                + "_papywizard.xml");
        log.info("Write papywizard file to: '{}'", file.getAbsolutePath());

        final String xml = papywizard.toXml();
        try {
            FileUtils.write(file, xml, Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Could not write papywizard file to: '{}'", file.getAbsolutePath(), e);
        }
    }
}
