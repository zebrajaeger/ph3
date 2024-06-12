package de.zebrajaeger.phserver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import de.zebrajaeger.phserver.data.ActorAxisStatus;
import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.hardware.Actor;
import de.zebrajaeger.phserver.hardware.fake.FakePanoHead;
import de.zebrajaeger.phserver.service.PanoHeadService;

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
    Actor actor;
    @Autowired
    PanoHeadService panoHeadService;

    @BeforeEach
    public void init() {
        if (FakePanoHead.class.equals(actor.getClass())) {
            ((FakePanoHead) actor).reset();
        }
    }

    @Test
    public void velocity() throws Exception {
        actor.setTargetVelocity(AxisIndex.X, 100);
        Thread.sleep(1000);

        ActorAxisStatus x = panoHeadService.getLatestPanoHeadData().getActorStatus().getX();
        assertThat(x.getPos(), allOf(greaterThan(90), lessThan(110)));
        assertThat(x.isMoving(), is(true));
        assertThat(x.isAtTargetPos(), is(false));
    }

    @Test
    public void pos() throws Exception {
        actor.setLimit(AxisIndex.X, 1000, 1000, 1000);
        actor.setTargetPos(AxisIndex.X, 750);
        Thread.sleep(1000);

        ActorAxisStatus x = panoHeadService.getLatestPanoHeadData().getActorStatus().getX();
        System.out.println(x);
        assertThat(x.getPos(), is(750));
        assertThat(x.isMoving(), is(false));
        assertThat(x.isAtTargetPos(), is(true));
    }
}
