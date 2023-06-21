package de.zebrajaeger.phserver.util;

import de.zebrajaeger.phserver.papywizard.Papywizard;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class PapywizardUtils {
    private static final SimpleDateFormat FILE_NAME_DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter CONTENT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH'h'mm'm'ss's'");


    public static void writePapywizardFile(Papywizard papywizard, String postfix) {
        final Date now = new Date();

        String name = papywizard.getHeader().getGeneral().getTitle();
        if (StringUtils.isBlank(name)) {
            name = "";
        } else {
            name = "_" + name;
        }

        String gps = papywizard.getHeader().getGeneral().getGps();
        if (gps == null) {
            gps = "";
        } else {
            gps = "_" + gps;
        }

        if (postfix == null) {
            postfix = "";
        } else {
            postfix = "_" + postfix;
        }

        final File file = new File(FILE_NAME_DATE_TIME_FORMATTER.format(now)
                + gps
                + name
                + postfix
                + "_papywizard.xml");
        log.info("Write papywizard file to: '{}'", file.getAbsolutePath());

        final String xml = papywizard.toXml();
        try {
            FileUtils.write(file, xml, Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Could not write papywizard file to: '{}'", file.getAbsolutePath(), e);
        }
    }

    /**
     * Result: "2014-02-23_13h59m01s"
     */
    public static String toString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(CONTENT_DATE_TIME_FORMATTER);
    }
}
