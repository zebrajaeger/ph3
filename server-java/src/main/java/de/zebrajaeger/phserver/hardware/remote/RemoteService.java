package de.zebrajaeger.phserver.hardware.remote;

import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.Joystick;
import de.zebrajaeger.phserver.hardware.JoystickDevice;
import de.zebrajaeger.phserver.hardware.PanoHead;
import de.zebrajaeger.phserver.hardware.PanoHeadDevice;
import de.zebrajaeger.phserver.util.HexUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@Profile({"remote"})
public class RemoteService implements HardwareService {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteService.class);

    @Value("${i2c.remote.host}")
    private String host;
    @Value("${i2c.address.joystick:0x30}")
    private int i2cJoystickAddress;
    @Value("${i2c.address.panohead:0x31}")
    private int i2cPanoHeadAddress;

    private Joystick joystick;
    private PanoHead panoHead;

    @PostConstruct
    public void init() {
        joystick = new JoystickDevice(new RemoteDevice(this, i2cJoystickAddress));
        panoHead = new PanoHeadDevice(new RemoteDevice(this, i2cPanoHeadAddress));
    }

    @Override
    public Joystick getJoystick() {
        return joystick;
    }

    @Override
    public PanoHead getPanoHead() {
        return panoHead;
    }

    protected byte[] read(int address, int count) throws IOException {
        Assert.state(address > 0, "address must be greater than zero");
        Assert.state(address < 128, "address must be less or equal 127");

        URL url = new URL(String.format("http://%s/read?address=%d&count=%d", host, address, count));
        LOG.trace("Request '{}'", url);
        String response = IOUtils.toString(url, StandardCharsets.UTF_8);
        return HexUtils.decodeHexString(response);
    }

    protected String write(int address, byte[] data) throws IOException {
        Assert.state(address > 0, "address must be greater than zero");
        Assert.state(address < 128, "address must be less or equal 127");

        String hex = HexUtils.encodeHexString(data);
        URL url = new URL(String.format("http://%s/write?address=%d&data=%s", host, address, hex));
        return IOUtils.toString(url, StandardCharsets.UTF_8);
    }
}
