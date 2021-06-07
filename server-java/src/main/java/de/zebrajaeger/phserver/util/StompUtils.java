package de.zebrajaeger.phserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class StompUtils {
    private StompUtils() {
    }

    public static void rpcSendResponse(SimpMessagingTemplate template, String id, String destination, Object o) throws JsonProcessingException {
        HashMap<String, Object> header = new HashMap<>();
        header.put("correlation-id", id);

        String message = MappingUtils.toJson(o);
        template.convertAndSend(destination, message, header);
    }
}
