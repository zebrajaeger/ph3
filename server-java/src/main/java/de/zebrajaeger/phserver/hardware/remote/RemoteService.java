package de.zebrajaeger.phserver.hardware.remote;

//@Service
//@Profile({"remote"})
@Deprecated
public class RemoteService {
//    private static final Logger LOG = LoggerFactory.getLogger(RemoteService.class);
//
//    private final FakeSystemControl systemDevice = new FakeSystemControl();
//
//    @Value("${i2c.remote.host}")
//    private String host;
//    @Value("${i2c.address.panohead:0x33}")
//    private int i2cPanoHeadAddress;
//    @Value("${i2c.address.gps:0x34}")
//    private int i2cGpsAddress;
//    @Value("${i2c.address.ina219:0x40}")
//    private int i2cIna219Address;
//    @Value("${i2c.address.adxl345:0x53}")
//    private int i2cAccelerationSensorAddress;
//
//    private PollingPanoHead pollingPanoHead;
//    private I2CGpsDevice gpsDevice;
//    private PowerGauge powerGauge;
//    private AccelerationSensor accelerationSensor;
//
//    @PostConstruct
//    public void init() {
//        pollingPanoHead = new PanoHeadDevice(new RemoteI2CConnection(this, i2cPanoHeadAddress));
//        gpsDevice = new I2CGpsDevice(new RemoteI2CConnection(this, i2cGpsAddress));
//        powerGauge = new I2CPowerGaugeIna219(new RemoteI2CConnection(this, i2cIna219Address));
//        accelerationSensor = new I2CAccelerationSensorAdxl345(
//                new RemoteI2CConnection(this, i2cAccelerationSensorAddress));
//    }
//
//    @Override
//    public PollingPanoHead getPanoHead() {
//        return pollingPanoHead;
//    }
//
//    @Override
//    public GpsDevice getGpsDevice() {
//        return gpsDevice;
//    }
//
//    @Override
//    public PowerGauge getPowerGauge() {
//        return powerGauge;
//    }
//
//    @Override
//    public Optional<AccelerationSensor> getAccelerationSensor() {
//        return Optional.of(accelerationSensor);
//    }
//
//    @Override
//    public SystemControl getSystemDevice() {
//        return systemDevice;
//    }
//
//    protected byte[] read(int address, int count) throws IOException {
//        Assert.state(address > 0, "address must be greater than zero");
//        Assert.state(address < 128, "address must be less or equal 127");
//
//        URL url = new URL(String.format("http://%s/read?address=%d&count=%d", host, address, count));
//        LOG.trace("Request '{}'", url);
//        String response = IOUtils.toString(url, StandardCharsets.UTF_8);
//        // TODO catch java.net.ConnectException and Restart connection
//        return HexUtils.decodeHexString(response);
//    }
//
//    @SuppressWarnings("UnusedReturnValue")
//    protected String write(int address, byte[] data) throws IOException {
//        Assert.state(address > 0, "address must be greater than zero");
//        Assert.state(address < 128, "address must be less or equal 127");
//
//        String hex = HexUtils.encodeHexString(data);
//        URL url = new URL(String.format("http://%s/write?address=%d&data=%s", host, address, hex));
//
//        // TODO catch java.net.ConnectException and Restart connection
//        return IOUtils.toString(url, StandardCharsets.UTF_8);
//    }
}
