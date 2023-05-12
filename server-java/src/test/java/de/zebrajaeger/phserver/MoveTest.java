package de.zebrajaeger.phserver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import de.zebrajaeger.phserver.data.ActorAxis;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.fake.FakeService;
import de.zebrajaeger.phserver.service.PanoHeadService;
import java.io.IOException;
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
public class MoveTest {

  @Autowired
  HardwareService hardwareService;
  @Autowired
  PanoHeadService panoHeadService;

  @BeforeEach
  public void init() {
    if (FakeService.class.equals(hardwareService.getClass())) {
      ((FakeService) hardwareService).reset();
    }
  }

  @Test
  public void velocity() throws InterruptedException, IOException {
    hardwareService.getPanoHead().setTargetVelocity(0, 100);
    Thread.sleep(1000);

    ActorAxis x = panoHeadService.getData().getActor().getX();
    assertThat(x.getPos(), allOf(greaterThan(90), lessThan(110)));
    assertThat(x.isMoving(), is(true));
    assertThat(x.isAtTargetPos(), is(false));
  }

  @Test
  public void pos() throws InterruptedException, IOException {
    hardwareService.getPanoHead().setLimit(0, 1000,1000,1000);
    hardwareService.getPanoHead().setTargetPos(0, 750);
    Thread.sleep(1000);

    ActorAxis x = panoHeadService.getData().getActor().getX();
    System.out.println(x);
    assertThat(x.getPos(), is(750));
    assertThat(x.isMoving(), is(false));
    assertThat(x.isAtTargetPos(), is(true));
  }
}
