package de.zebrajaeger.phserver.hardware.remote;

import de.zebrajaeger.phserver.hardware.AccelerationSensor;
import de.zebrajaeger.phserver.hardware.AccelerationSensorDeviceAdxl345;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.PanoHead;
import de.zebrajaeger.phserver.hardware.PanoHeadDevice;
import de.zebrajaeger.phserver.hardware.PowerGauge;
import de.zebrajaeger.phserver.hardware.PowerGaugeDeviceIna219;
import de.zebrajaeger.phserver.hardware.SystemDevice;
import de.zebrajaeger.phserver.hardware.fake.FakeSystemDevice;
import de.zebrajaeger.phserver.util.HexUtils;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Profile({"remote"})
public class RemoteService implements HardwareService {

  private static final Logger LOG = LoggerFactory.getLogger(RemoteService.class);

  private final FakeSystemDevice systemDevice = new FakeSystemDevice();

  @Value("${i2c.remote.host}")
  private String host;
  @Value("${i2c.address.panohead:0x33}")
  private int i2cPanoHeadAddress;
  @Value("${i2c.address.ina219:0x40}")
  private int i2cIna219Address;
  @Value("${i2c.address.adxl345:0x53}")
  private int i2cAccelerationSensorAddress;

  private PanoHead panoHead;
  private PowerGauge powerGauge;
  private AccelerationSensor accelerationSensor;

  @PostConstruct
  public void init() {
    panoHead = new PanoHeadDevice(new RemoteDevice(this, i2cPanoHeadAddress));
    powerGauge = new PowerGaugeDeviceIna219(new RemoteDevice(this, i2cIna219Address));
    accelerationSensor = new AccelerationSensorDeviceAdxl345(
        new RemoteDevice(this, i2cAccelerationSensorAddress));
  }

  @Override
  public PanoHead getPanoHead() {
    return panoHead;
  }

  @Override
  public Optional<PowerGauge> getPowerGauge() {
    return Optional.of(powerGauge);
  }

  @Override
  public Optional<AccelerationSensor> getAccelerationSensor() {
    return Optional.of(accelerationSensor);
  }

  @Override
  public SystemDevice getSystemDevice() {
    return systemDevice;
  }

  protected byte[] read(int address, int count) throws IOException {
    Assert.state(address > 0, "address must be greater than zero");
    Assert.state(address < 128, "address must be less or equal 127");

    URL url = new URL(String.format("http://%s/read?address=%d&count=%d", host, address, count));
    LOG.trace("Request '{}'", url);
    String response = IOUtils.toString(url, StandardCharsets.UTF_8);
    // TODO catch java.net.ConnectException and Restart connection
    return HexUtils.decodeHexString(response);
  }

  @SuppressWarnings("UnusedReturnValue")
  protected String write(int address, byte[] data) throws IOException {
    Assert.state(address > 0, "address must be greater than zero");
    Assert.state(address < 128, "address must be less or equal 127");

    String hex = HexUtils.encodeHexString(data);
    URL url = new URL(String.format("http://%s/write?address=%d&data=%s", host, address, hex));

    // TODO catch java.net.ConnectException and Restart connection
    return IOUtils.toString(url, StandardCharsets.UTF_8);
  }
}
