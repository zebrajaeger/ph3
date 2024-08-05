package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.CameraStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Profile({"ccapi-cam"})
@Service
@Slf4j
public class CcApiCamera implements Camera {
    @Value("${camera.ccapi.timeout:4000}")
    private int requestTimeout;

    @Value("${camera.ccapi.url:http://192.168.8.149:8080/ccapi}")
    private String apiUrl;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CcApiCamera(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void startFocus(int focusTimeMs) throws Exception {
        // for a happy logic
        applicationEventPublisher.publishEvent(new CameraStatus(true, false));
        applicationEventPublisher.publishEvent(new CameraStatus(false, false));
    }

    @Override
    public void startTrigger(int triggerTimeMs) throws Exception {
        log.debug("startTrigger({})", triggerTimeMs);
        applicationEventPublisher.publishEvent(new CameraStatus(false, true));
        try {
            post("/ver100/shooting/control/shutterbutton", "{\"af\":false}");
        } catch (Exception e) {
            log.error("Could not trigger: {}", e.getMessage());
            log.debug("Could not trigger", e);
            throw e;
        } finally {
            applicationEventPublisher.publishEvent(new CameraStatus(false, false));
        }
    }

    @Override
    public void startShot(int focusTimeMs, int triggerTimeMs) throws Exception {
        log.debug("startShot({},{})", focusTimeMs, triggerTimeMs);
        // for a happy logic
        applicationEventPublisher.publishEvent(new CameraStatus(true, false));
        applicationEventPublisher.publishEvent(new CameraStatus(false, true));
        try {
            post("/ver100/shooting/control/shutterbutton", "{\"af\":false}");
        } catch (Exception e) {
            log.error("Could not shot: {}", e.getMessage());
            log.debug("Could not shot", e);
            throw e;
        } finally {
            applicationEventPublisher.publishEvent(new CameraStatus(false, false));
        }
    }

    private String get(String id) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(apiUrl + id);
            log.debug("GET {}", uri);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String post(String id, String body) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create(apiUrl + id);
            log.info("POST {}: {}", uri, body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofMillis(requestTimeout))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
