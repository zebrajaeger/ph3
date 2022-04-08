package de.zebrajaeger.phserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        String dir = new File("./lib").getAbsolutePath();
        System.out.println("Lib path: '" + dir + "'");
        System.setProperty("net.java.games.input.librarypath", dir);

        SpringApplication.run(App.class, args);
    }
}
