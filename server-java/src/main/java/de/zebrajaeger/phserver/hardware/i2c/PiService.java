package de.zebrajaeger.phserver.hardware.i2c;

//@Service
//@Profile({"pi", "default"})
@Deprecated
public class PiService {

//    private static final Logger LOG = LoggerFactory.getLogger(RemoteService.class);

    /**
     * #define I2C_ADDRESS 0x33
     */
//    @Value("${i2c.bus:0x01}")
//    private int i2cBus;
//    @Value("${i2c.address.panohead:0x33}")
//    private int i2cPanoHeadAddress;
//    @Value("${i2c.address.gps:0x34}")
//    private int i2cGpsAddress;
//    @Value("${i2c.address.ina219:0x40}")
//    private int i2cIna219Address;
//    @Value("${i2c.address.adxl345:0x53}")
//    private int i2cAccelerationSensorAddress;

//    private PollingPanoHead pollingPanoHead;
//    private I2CGpsDevice gpsDevice;
//    private PowerGauge powerGauge;
//    private AccelerationSensor accelerationSensor;
//    private PiSystem piSystem;

//    @PostConstruct
//    public void init() throws IOException, I2CFactory.UnsupportedBusNumberException {
//        final Console console = new Console();
//        int[] ids = I2CFactory.getBusIds();
//        console.println("Found follow I2C busses: " + Arrays.toString(ids));
//        I2CBus bus = I2CFactory.getInstance(i2cBus);
//        scan(bus);
//        pollingPanoHead = new PanoHeadDevice(new LocalI2CConnection(bus.getDevice(i2cPanoHeadAddress)));
//        gpsDevice = new I2CGpsDevice(new LocalI2CConnection(bus.getDevice(i2cGpsAddress)));
//        powerGauge = new I2CPowerGaugeIna219(new LocalI2CConnection(bus.getDevice(i2cIna219Address)));
//        accelerationSensor = new I2CAccelerationSensorAdxl345(
//                new LocalI2CConnection(bus.getDevice(i2cAccelerationSensorAddress)));
//        piSystem = new PiSystem();
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
//    public SystemDevice getSystemDevice() {
//        return piSystem;
//    }
//
//    private void scan(I2CBus bus) {
//        LOG.info(String.format("Scan Bus: %d", bus.getBusNumber()));
//        LOG.info("     0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f");
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < 128; i++) {
//            String iAsHex = String.format("%02X", i);
//            if (i % 16 == 0) {
//                LOG.info(sb.toString());
//                sb.setLength(0);
//                sb.append(iAsHex).append(": ");
//            }
//
//            if (i > 0) {
//                try {
//                    I2CDevice device = bus.getDevice(i);
//                    device.write((byte) 0);
//                    sb.append(iAsHex).append(" ");
//                } catch (Exception ignore) {
//                    sb.append("-- ");
//                }
//            } else {
//                sb.append("   ");
//            }
//        }
//        LOG.info(sb.toString());
//    }
}
