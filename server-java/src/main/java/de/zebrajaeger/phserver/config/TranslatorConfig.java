package de.zebrajaeger.phserver.config;

import de.zebrajaeger.phserver.Translator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TranslatorConfig {
    @Bean
    public Translator translator() {
        return new Translator();
    }
}
