package de.zebrajaeger.phserver.stomp;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.zebrajaeger.phserver.service.PanoHeadService;
import de.zebrajaeger.phserver.data.Camera;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.event.CameraChangedEvent;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.util.StompUtils;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CameraSTOMPController {

  private final PanoHeadService panoHeadService;
  private final HardwareService hardwareService;
  private final SimpMessagingTemplate template;

  private Camera camera = new Camera();

  @Autowired
  public CameraSTOMPController(PanoHeadService deviceService, HardwareService hardwareService,
      SimpMessagingTemplate template) {
    this.panoHeadService = deviceService;
    this.hardwareService = hardwareService;
    this.template = template;
  }

  @MessageMapping("/camera")
  public void getCameraStateRpc(@Header("correlation-id") String id,
      @Header("reply-to") String destination) {
    HashMap<String, Object> header = new HashMap<>();
    header.put("correlation-id", id);
    template.convertAndSend(destination, panoHeadService.getData().getCamera(), header);
  }

  @MessageMapping("camera/focus")
  public void focus(@Payload int focusTimeMs) throws IOException {
    hardwareService.getPanoHead().startFocus(focusTimeMs);
  }

  @MessageMapping("camera/trigger")
  public void trigger(@Payload int triggerTimeMs) throws IOException {
    hardwareService.getPanoHead().startTrigger(triggerTimeMs);
  }

  @MessageMapping("camera/shot")
  public void trigger(@Payload Shot shot) throws IOException {
    hardwareService.getPanoHead().startShot(shot.getFocusTimeMs(), shot.getTriggerTimeMs());
  }

  @EventListener
  public void onPanoHeadChanged(CameraChangedEvent cameraChangedEvent) {
    camera = cameraChangedEvent.getCamera();
    template.convertAndSend("/topic/camera", camera);
  }

  @MessageMapping("/rpc/camera")
  public void rpcCamera(@Header("correlation-id") String id, @Header("reply-to") String destination)
      throws JsonProcessingException {
    StompUtils.rpcSendResponse(template, id, destination, camera);
  }
}
