package de.zebrajaeger.phserver.hardware.fake;

import de.zebrajaeger.phserver.data.RawPosition;
import de.zebrajaeger.phserver.hardware.Joystick;
import org.lwjgl.glfw.GLFW;
import org.springframework.util.Assert;

import java.nio.FloatBuffer;

public class FakeJoystick implements Joystick {
    private static final int RESOLUTION = 1024;

    private int joystickIndex = GLFW.GLFW_JOYSTICK_1;
    private int xAxisIndex = 0;
    private int yAxisIndex = 1;

    public FakeJoystick() {
    }

    public FakeJoystick(int joystickIndex, int xAxisIndex, int yAxisIndex) {
        this.joystickIndex = joystickIndex;
        this.xAxisIndex = xAxisIndex;
        this.yAxisIndex = yAxisIndex;
    }

    @Override
    public RawPosition read() {
        FloatBuffer fb = GLFW.glfwGetJoystickAxes(joystickIndex);
        if(fb==null){
            return new RawPosition(0,0);
        }
        //Assert.notNull(fb, "No Joystick found for id: " + joystickIndex);
        return new RawPosition((int) (fb.get(xAxisIndex) * RESOLUTION), (int) (fb.get(yAxisIndex) * RESOLUTION));
    }

    public void init() {
        GLFW.glfwInit();
    }

    public void destroy() {
        GLFW.glfwTerminate();
    }
}
