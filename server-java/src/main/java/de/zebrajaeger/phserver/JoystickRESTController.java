package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.JoystickPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class JoystickRESTController {

    private final JoystickService joystickService;

    @Autowired
    public JoystickRESTController(JoystickService joystickService) {
        this.joystickService = joystickService;
    }

    @GetMapping("/joystick")
    public JoystickPosition getJoystickPosition() {
        return joystickService.getPosition();
    }

    @PutMapping("/joystick/center")
    public void center() {
        joystickService.setCurrentPositionAsCenter();
    }

    @PutMapping("/joystick/reset")
    public void reset() {
        joystickService.reset();
    }
}
