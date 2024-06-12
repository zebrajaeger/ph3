package de.zebrajaeger.phserver.hardware.mqtt;

import de.zebrajaeger.phserver.hardware.*;
import de.zebrajaeger.phserver.hardware.fake.FakeGpsReceiver;
import de.zebrajaeger.phserver.hardware.fake.FakePowerGauge;
import de.zebrajaeger.phserver.hardware.local.LinuxSystemControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Profile("mqtt")
@Service
@Slf4j
@Deprecated
public class MqttService implements HardwareService {

//    private final PowerGauge powerGauge = new FakePowerGauge();
//    private final SystemControl systemControl = new LinuxSystemControl();
//    private final FakeGpsReceiver gpsDevice = new FakeGpsReceiver();
//    private final MqttPanoHead mqttPanoHead;
//
//    public MqttService(MqttPanoHead mqttPanoHead) {
//        this.mqttPanoHead = mqttPanoHead;
//    }
//
//    @Override
//    public PollingPanoHead getPanoHead() {
//        return mqttPanoHead;
//    }
//
//    @Override
//    public PowerGauge getPowerGauge() {
//        return powerGauge;
//    }
//
//    @Override
//    public Optional<AccelerationSensor> getAccelerationSensor() {
//        return Optional.empty();
//    }
//
//    @Override
//    public SystemControl getSystemDevice() {
//        return systemControl;
//    }
//
//    @Override
//    public GpsDevice getGpsDevice() {
//        return gpsDevice;
//    }
}
