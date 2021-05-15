package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.AxisValue;
import de.zebrajaeger.phserver.data.PanoHeadData;
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

    @MessageMapping("/actor")
    public void getActorRpc(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        HashMap<String, Object> header = new HashMap<>();
        header.put("correlation-id", id);
        template.convertAndSend(destination, panoHeadService.getData().getActor(), header);
    }

    @MessageMapping("actor/limit")
    public void limit(@Payload AxisValue limit) throws IOException {
        hardwareService.getPanoHead().setLimit(limit.getAxisIndex(), limit.getValue());
    }

    @MessageMapping("actor/pos/{axisIndex}")
    public void pos(@Payload AxisValue pos) throws IOException {
        hardwareService.getPanoHead().setTargetPos(pos.getAxisIndex(), pos.getValue());
    }

    @MessageMapping("actor/velocity/{axisIndex}")
    public void velocity(@Payload AxisValue velocity) throws IOException {
        hardwareService.getPanoHead().setTargetVelocity(velocity.getAxisIndex(), velocity.getValue());
    }

    @MessageMapping("/actor/jogging")
    public void setJogging(@Payload boolean jogging) throws IOException {
        panoHeadService.setJogging(jogging);
    }

    @EventListener
    public void onPanoHeadChanged(PanoHeadData panoHeadData) {
        template.convertAndSend("/topic/actor/", panoHeadData.getActor());
    }
}
