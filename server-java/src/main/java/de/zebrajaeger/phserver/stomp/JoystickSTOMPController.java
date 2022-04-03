package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.JoystickService;
import de.zebrajaeger.phserver.data.JoystickPosition;
import de.zebrajaeger.phserver.event.JoystickPositionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;

@Controller
public class JoystickSTOMPController {

    private final JoystickService joystickService;
    private final SimpMessagingTemplate template;

    @Autowired
    public JoystickSTOMPController(SimpMessagingTemplate template, JoystickService joystickService) {
        this.template = template;
        this.joystickService = joystickService;
    }

    @MessageMapping("/joystick/position")
    public void getJoystickPositionRpc(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        HashMap<String, Object> header = new HashMap<>();
        header.put("correlation-id", id);
        template.convertAndSend(destination, joystickService.getPosition(), header);
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
        template.convertAndSend("/topic/joystick/position", joystickPosition.getPosition());
    }
}
