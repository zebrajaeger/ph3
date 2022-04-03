package de.zebrajaeger.phserver.rest;

import de.zebrajaeger.phserver.JoystickService;
import de.zebrajaeger.phserver.data.JoystickPosition;
import de.zebrajaeger.phserver.data.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api")
public class JoystickRESTController {

    private final JoystickService joystickService;

    @Autowired
    public JoystickRESTController(JoystickService joystickService) {
        this.joystickService = joystickService;
    }

    @GetMapping("/joystick")
    public Position getJoystickPosition() {
        return joystickService.getPosition();
    }

//    @PutMapping("/joystick/center")
//    public void center() throws IOException {
//        joystickService.setCurrentPositionAsCenter();
//    }
//
//    @PutMapping("/joystick/reset")
//    public void reset() throws IOException {
//        joystickService.reset();
//    }
}
