package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.event.JoystickPositionEvent;
import de.zebrajaeger.phserver.service.PanoHeadService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
public class JoystickSTOMPController {

    private final SimpMessagingTemplate template;
//    private final ApplicationEventPublisher applicationEventPublisher;
    private Position joystickPosition = null;
    private final PanoHeadService panoHeadService;

    public JoystickSTOMPController(SimpMessagingTemplate template,
//                                   ApplicationEventPublisher applicationEventPublisher,
                                   PanoHeadService panoHeadService) {
        this.template = template;
//        this.applicationEventPublisher = applicationEventPublisher;
        this.panoHeadService = panoHeadService;
    }

    @EventListener
    public void onJoystick(JoystickPositionEvent event) {
        joystickPosition = event.position();
    }

    @MessageMapping("/joystick/position")
    public void getJoystickPositionRpc(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        HashMap<String, Object> header = new HashMap<>();
        header.put("correlation-id", id);
        template.convertAndSend(destination, joystickPosition, header);
    }

    @MessageMapping("/deviceorientation/position")
    public void onDeviceOrientationPosition(@Payload Position position) {
//        Position p2 = position.divide(10);
//        System.out.println(position);
        panoHeadService.manualMoveByJoystickWithEmergencyStopOnTimeout(position);
    }

//    @MessageMapping("/joystick/center")
//    public void center() throws IOException {
//        joystickService.setCurrentPositionAsCenter();
//    }
//
//    @MessageMapping("/joystick/reset")
//    public void reset() throws IOException {
//        joystickService.reset();
//    }

    @EventListener
    public void onJoystickPosChanged(JoystickPositionEvent joystickPosition) {
        template.convertAndSend("/topic/joystick/position", joystickPosition.position());
    }
}
