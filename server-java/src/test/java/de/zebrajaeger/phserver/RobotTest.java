package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.fake.FakeService;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.TakeShotCommand;
import de.zebrajaeger.phserver.pano.WaitCommand;
import de.zebrajaeger.phserver.service.RobotService;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    commands.add(new WaitCommand(null, "wait...", 500));
    robotService.start(commands);
    Thread.sleep(1000);
  }

  @Test
  public void singleShot() throws InterruptedException {
    List<Command> commands = new LinkedList<>();
    commands.add(new TakeShotCommand(null, "shot...", new Shot(500, 800)));
    robotService.start(commands);
    Thread.sleep(2000);
  }
}
