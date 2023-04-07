package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.PanoHeadService;
import de.zebrajaeger.phserver.data.AxisValue;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.event.JoggingChangedEvent;
import de.zebrajaeger.phserver.event.PositionEvent;
import de.zebrajaeger.phserver.event.PowerMeasureEvent;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.PanoHead;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class PanoHeadSTOMPController {

  private final PanoHeadService panoHeadService;
  private final HardwareService hardwareService;
  private final SimpMessagingTemplate template;

  @Autowired
  public PanoHeadSTOMPController(PanoHeadService deviceService, HardwareService hardwareService,
      SimpMessagingTemplate template) {
    this.panoHeadService = deviceService;
    this.hardwareService = hardwareService;
    this.template = template;
  }

  //<editor-fold desc="Actor">
  @MessageMapping("/actor")
  public void getActorRpc(@Header("correlation-id") String id,
      @Header("reply-to") String destination) {
    HashMap<String, Object> header = new HashMap<>();
    header.put("correlation-id", id);
    template.convertAndSend(destination, panoHeadService.getData().getActor(), header);
  }

  @MessageMapping("/actor/limit")
  public void limit(@Payload AxisValue limit) throws IOException {
    final Optional<PanoHead> panoHead = hardwareService.getPanoHead();
    if (panoHead.isPresent()) {
      panoHead.get().setLimit(limit.getAxisIndex(), limit.getValue());
    }
  }

  @EventListener
  public void onPanoHeadChanged(PositionEvent positionEvent) {
    template.convertAndSend("/topic/actor/position/", positionEvent.getPosition());
  }
  //</editor-fold>

  //<editor-fold desc="Control">

  @MessageMapping("/actor/setToZero")
  public void setToZero() throws IOException {
    panoHeadService.setToZero();
  }

  @MessageMapping("/actor/jogging")
  public void setJogging(@Payload boolean jogging) throws IOException {
    panoHeadService.setJogging(jogging);
  }

  @MessageMapping("/actor/manualMove")
  public void manualMove(@Payload Position relPosition) throws IOException {
    panoHeadService.manualMove(relPosition);
  }

  @EventListener
  public void onJoggingChanged(JoggingChangedEvent joggingChangedEvent) {
    template.convertAndSend("/topic/actor/jogging/", joggingChangedEvent.isJogging());
  }
  //</editor-fold>

  @EventListener
  public void onPowerConsumption(PowerMeasureEvent powerMeasureEvent) {
    template.convertAndSend("/topic/power/", powerMeasureEvent.getPower());
  }
}
