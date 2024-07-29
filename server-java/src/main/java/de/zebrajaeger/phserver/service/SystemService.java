package de.zebrajaeger.phserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SystemService {
    @Scheduled(fixedRateString = "10000")
    public void onShowMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        log.info("Total Memory: {} Free Memory: {} Used Memory: {} Max Memory: {}",
                formatBytes(totalMemory),
                formatBytes(freeMemory),
                formatBytes(usedMemory),
                formatBytes(maxMemory));
    }

    private static String formatBytes(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + ("i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
