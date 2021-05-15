package de.zebrajaeger.phserver;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;

public class JoystickTest {
    @Test
    public void foo() {
        // no crash is already a good start...

        // TODO buy joystick to test this and use it for Fake Joystick...
        String name = GLFW.glfwGetJoystickName(GLFW.GLFW_JOYSTICK_1);
        System.out.println(name);
    }
}
