package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupApplicationListener {
    private final CommonService commonService;

    public StartupApplicationListener(CommonService commonService) {
        this.commonService = commonService;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent ignoredEvent) {
        log.info("{}", commonService.getVersion());
    }
}
