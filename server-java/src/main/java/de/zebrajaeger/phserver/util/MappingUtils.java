package de.zebrajaeger.phserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class MappingUtils {
    private final static ObjectMapper mapper = new ObjectMapper();

    private MappingUtils() {
    }

    public static String toJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }
}
