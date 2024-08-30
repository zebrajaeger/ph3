package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.hardware.udp.CommandType;
import de.zebrajaeger.phserver.hardware.udp.UdpCommand;
import de.zebrajaeger.phserver.hardware.udp.WSService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("enable.actor.ws")
public class WSActor implements Actor {
    private final WSService wsService;

    public WSActor(WSService wsService) {
        this.wsService = wsService;
    }

    @Override
    public void setLimit(AxisIndex axisIndex, int velocityMinHz, int velocityMaxHz, int accelerationMaxHzPerSecond) throws Exception {

    }

    @Override
    public void setTargetVelocity(AxisIndex axisIndex, int velocity) throws Exception {
        UdpCommand cmd = new UdpCommand();
        if (axisIndex == AxisIndex.X) {
            cmd.setType(CommandType.SPEED_X);
            cmd.setSpeedX(velocity);
        } else if (axisIndex == AxisIndex.Y) {
            cmd.setType(CommandType.SPEED_Y);
            cmd.setSpeedY(velocity);
        }
        wsService.sendCommand(cmd);
    }

    @Override
    public void setTargetPos(AxisIndex axisIndex, int pos) throws Exception {
        UdpCommand cmd = new UdpCommand();
        if (axisIndex == AxisIndex.X) {
            cmd.setType(CommandType.MOVE_TO_X);
            cmd.setPosX(pos);
            cmd.setSpeedX(1000);
        } else if (axisIndex == AxisIndex.Y) {
            cmd.setType(CommandType.MOVE_TO_Y);
            cmd.setPosY(pos);
            cmd.setSpeedY(1000);
        }
        wsService.sendCommand(cmd);
    }

    @Override
    public void stopAll() throws Exception {
        UdpCommand cmd = new UdpCommand();
        cmd.setType(CommandType.STOP_XY);
        wsService.sendCommand(cmd);
    }

    @Override
    public void setActualAndTargetPos(AxisIndex axisIndex, int pos) throws Exception {
        UdpCommand cmd = new UdpCommand();
        if (axisIndex == AxisIndex.X) {
            cmd.setType(CommandType.SET_POS_X);
            cmd.setPosX(pos);
        } else if (axisIndex == AxisIndex.Y) {
            cmd.setType(CommandType.SET_POS_Y);
            cmd.setPosY(pos);
        }
        wsService.sendCommand(cmd);
    }

    @Override
    public void resetPos() throws Exception {
        UdpCommand cmd = new UdpCommand();
        cmd.setType(CommandType.SET_POS_XY);
        cmd.setPosX(0);
        cmd.setPosY(0);
        wsService.sendCommand(cmd);
    }
}
