package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.RawPosition;
import de.zebrajaeger.phserver.hardware.HardwareService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("remote")
public class RemoteTest {
    @Autowired
    HardwareService hardwareService;

    @Test
    public void foo() throws IOException, InterruptedException {
        while(true){
        RawPosition pos = hardwareService.getJoystick().read();
            System.out.println(pos);
            Thread.sleep(250);
        }
    }
}
