package de.zebrajaeger.phserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class App {
    public static void main(String[] args) {
        String dir = new File("./lib").getAbsolutePath();
        log.info("Lib path: '{}'", dir);
        System.setProperty("net.java.games.input.librarypath", dir);

        SpringApplication.run(App.class, args);
    }
}
