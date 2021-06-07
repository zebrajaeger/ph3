package de.zebrajaeger.phserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class MappingUtils {
    private final static ObjectMapper mapper;

    private MappingUtils() {
    }

    static {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static String toJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }
}
