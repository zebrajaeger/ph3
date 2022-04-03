package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.PanoHeadService;
import de.zebrajaeger.phserver.data.AxisValue;
import de.zebrajaeger.phserver.event.JoggingChangedEvent;
import de.zebrajaeger.phserver.event.PanoHeadDataEvent;
import de.zebrajaeger.phserver.event.PositionEvent;
import de.zebrajaeger.phserver.event.PowerMeasureEvent;
import de.zebrajaeger.phserver.hardware.HardwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;

@Controller
public class PanoHeadSTOMPController {
    private final PanoHeadService panoHeadService;
    private final HardwareService hardwareService;
    private final SimpMessagingTemplate template;

    @Autowired
    public PanoHeadSTOMPController(PanoHeadService deviceService, HardwareService hardwareService, SimpMessagingTemplate template) {
        this.panoHeadService = deviceService;
        this.hardwareService = hardwareService;
        this.template = template;
    }

    //<editor-fold desc="Actor">
    @MessageMapping("/actor")
    public void getActorRpc(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        HashMap<String, Object> header = new HashMap<>();
        header.put("correlation-id", id);
        template.convertAndSend(destination, panoHeadService.getData().getActor(), header);
    }

    @MessageMapping("/actor/limit")
    public void limit(@Payload AxisValue limit) throws IOException {
        hardwareService.getPanoHead().setLimit(limit.getAxisIndex(), limit.getValue());
    }

//    @MessageMapping("/actor/pos")
//    public void pos(@Payload AxisValue pos) throws IOException {
//        hardwareService.getPanoHead().setTargetPos(pos.getAxisIndex(), pos.getValue());
//    }
//
//    @MessageMapping("/actor/velocity")
//    public void velocity(@Payload AxisValue velocity) throws IOException {
//        hardwareService.getPanoHead().setTargetVelocity(velocity.getAxisIndex(), velocity.getValue());
//    }

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
