package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.fake.FakeService;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.GoToPosCommand;
import de.zebrajaeger.phserver.pano.RobotService;
import de.zebrajaeger.phserver.pano.TakeShotCommand;
import de.zebrajaeger.phserver.pano.WaitCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("develop")
public class RobotTest {

    @Autowired
    private RobotService robotService;
    @Autowired
    HardwareService hardwareService;

    @BeforeEach
    public void init() {
        if (FakeService.class.equals(hardwareService.getClass())) {
            ((FakeService) hardwareService).reset();
        }
    }

    @Test
    public void startWithNoCommands() throws InterruptedException {
        List<Command> commands = new LinkedList<>();
        robotService.start(commands);
        Thread.sleep(1000);
    }

    @Test
    public void singleDelay() throws InterruptedException {
        List<Command> commands = new LinkedList<>();
        commands.add(new WaitCommand(0, "wait...", 500));
        robotService.start(commands);
        Thread.sleep(1000);
    }

    @Test
    public void singleShot() throws InterruptedException {
        List<Command> commands = new LinkedList<>();
        commands.add(new TakeShotCommand(0, "shot...", new Shot(500, 800)));
        robotService.start(commands);
        Thread.sleep(2000);
    }

    @Test
    public void singleMove() throws InterruptedException {
        List<Command> commands = new LinkedList<>();
        commands.add(new GoToPosCommand(0, "shot...", new Position(0.25,0)));
        robotService.start(commands);
        Thread.sleep(10000);
    }
}
