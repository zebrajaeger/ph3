package de.zebrajaeger.phserver.config;

import de.zebrajaeger.phserver.hardware.battery.Batteries;
import de.zebrajaeger.phserver.hardware.battery.BatteryInterpolator;
import de.zebrajaeger.phserver.hardware.battery.DefaultBatteryInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HardwareConfig {
    @Bean
    public BatteryInterpolator batteryInterpolator() {
        return new DefaultBatteryInterpolator(Batteries.ParksideX20);
    }
}
