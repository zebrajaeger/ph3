package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.event.ActorActiveChangedEvent;
import de.zebrajaeger.phserver.event.JoggingChangedEvent;
import de.zebrajaeger.phserver.event.PositionEvent;
import de.zebrajaeger.phserver.event.PowerMeasureEvent;
import de.zebrajaeger.phserver.service.PanoHeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
@Slf4j
public class PanoHeadSTOMPController {

    private final PanoHeadService panoHeadService;
    private final SimpMessagingTemplate template;

    public PanoHeadSTOMPController(PanoHeadService deviceService, SimpMessagingTemplate template) {
        this.panoHeadService = deviceService;
        this.template = template;
    }

    //<editor-fold desc="Actor">
    @MessageMapping("/actor")
    public void getActorRpc(
            @Header("correlation-id") String id,
            @Header("reply-to") String destination) {

        HashMap<String, Object> header = new HashMap<>();
        header.put("correlation-id", id);
        template.convertAndSend(destination, panoHeadService.getData().getActor(), header);
    }

    @EventListener
    public void onPanoHeadChanged(PositionEvent positionEvent) {
        template.convertAndSend("/topic/actor/position/", positionEvent.position());
    }

    @EventListener
    public void onActorActiveChanged(ActorActiveChangedEvent actorActiveChangedEvent) {
        template.convertAndSend("/topic/actor/active/", actorActiveChangedEvent.isActive());
    }
    //</editor-fold>

    //<editor-fold desc="Control">

    @MessageMapping("/actor/setToZero")
    public void setToZero() {
        panoHeadService.setToZero();
    }

    @MessageMapping("/actor/goToZero")
    public void goToZero() {
        panoHeadService.manualAbsoluteMove(new Position(0, 0));
    }

    @MessageMapping("/actor/adaptOffset")
    public void adaptOffset() {
        panoHeadService.adaptAxisOffset();
    }

    @MessageMapping("/actor/jogging")
    public void setJogging(@Payload boolean jogging) {
        panoHeadService.setJoggingEnabled(jogging);
    }

    @MessageMapping("/actor/manualMove")
    public void manualMove(@Payload Position relPosition) {
        panoHeadService.manualRelativeMove(relPosition);
    }

    @MessageMapping("/actor/manualMoveByJoystick")
    public void manualMoveByJoystick(@Payload Position relSpeed) {
        panoHeadService.manualMoveByJoystickWithEmergencyStopOnTimeout(relSpeed);
    }

    @MessageMapping("/actor/manualMoveByJoystickStop")
    public void manualMoveByJoystickStop() {
        panoHeadService.manualMoveByJoystickStop();
    }

    @EventListener
    public void onJoggingChanged(JoggingChangedEvent joggingChangedEvent) {
        template.convertAndSend("/topic/actor/jogging/", joggingChangedEvent.jogging());
    }
    //</editor-fold>

    @EventListener
    public void onPowerConsumption(PowerMeasureEvent powerMeasureEvent) {
        template.convertAndSend("/topic/power/", powerMeasureEvent.power());
    }
}
