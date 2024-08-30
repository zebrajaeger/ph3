package de.zebrajaeger.phserver.stomp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Objects;

@Controller
@Slf4j
public class DebugSTOMPController {
    private final SimpMessagingTemplate template;

    public DebugSTOMPController(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * OK
     */
    @MessageMapping("/foo")
    public String greeting(String message) throws Exception {
//        Thread.sleep(1000); // simulated delay
        return "\"Hello\"";
    }

    /**
     * OK
     */
    @Scheduled(fixedRateString = "1000")
    public void foo() {
        template.convertAndSend("/topic/greetings", "1");
    }

    /**
     * F....
     */
    @Scheduled(fixedRateString = "1000")
    public void foo2() {
        template.convertAndSend("/topic/greetings/", "2");
    }

    /**
     * F....
     */
    @SendTo("/topic/greetings")
    @Scheduled(fixedRateString = "1000")
    public String foo3() {
        return "3";
    }

    @MessageMapping("/rpc/1")
    public void rpc1(SimpMessageHeaderAccessor accessor) {
//        accessor.getFirstNativeHeader("reply-to")

        template.convertAndSend(Objects.requireNonNull(accessor.getFirstNativeHeader("reply-to")),
                "XXX",
                new HashMap<>(accessor.toNativeHeaderMap()));
    }

}
