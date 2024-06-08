package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.fake.FakeService;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.TakeShotCommand;
import de.zebrajaeger.phserver.pano.WaitCommand;
import de.zebrajaeger.phserver.papywizard.Papywizard;
import de.zebrajaeger.phserver.service.RecordService;
import de.zebrajaeger.phserver.settings.ShotSettings;
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
    private RecordService recordService;
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
        recordService.requestStart(commands, new Papywizard());
        Thread.sleep(1000);
    }

    @Test
    public void singleDelay() throws InterruptedException {
        List<Command> commands = new LinkedList<>();
        commands.add(new WaitCommand(null, "wait...", 500));
        recordService.requestStart(commands, new Papywizard());
        Thread.sleep(1000);
    }

    @Test
    public void singleShot() throws InterruptedException {
        List<Command> commands = new LinkedList<>();
        commands.add(new TakeShotCommand(null, "shot...", new ShotSettings(500, 800), 0));
        recordService.requestStart(commands, new Papywizard());
        Thread.sleep(2000);
    }
}
