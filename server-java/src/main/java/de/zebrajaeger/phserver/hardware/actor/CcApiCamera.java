package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.CameraStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Profile({"ccapi"})
@Service
@Slf4j
public class CcApiCamera implements Camera {
    private String apiUrl = "http://192.168.8.149:8080/ccapi"; // TODO make configurable and refactor to CcApi class
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
        applicationEventPublisher.publishEvent(new CameraStatus(false, true));
        try {
            post("/ver100/shooting/control/shutterbutton", "{\"af\":false}");
        } finally {
            applicationEventPublisher.publishEvent(new CameraStatus(false, false));
        }
    }

    @Override
    public void startShot(int focusTimeMs, int triggerTimeMs) throws Exception {
        // for a happy logic
        applicationEventPublisher.publishEvent(new CameraStatus(true, false));
        applicationEventPublisher.publishEvent(new CameraStatus(false, true));
        try {
            post("/ver100/shooting/control/shutterbutton", "{\"af\":false}");
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
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            String responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return responseBody;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
