package io.buzzy.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.buzzy.common.exception.JsonUtilException;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

    public static String toJson(Object obj) throws JsonUtilException {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonUtilException("Error converting object to JSON string", e);
        }
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) throws JsonUtilException {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (JsonMappingException e) {
            throw new JsonUtilException("Error mapping JSON string to object", e);
        } catch (JsonProcessingException e) {
            throw new JsonUtilException("Error processing JSON string", e);
        }
    }

    public static <T> T fromJson(String jsonString, TypeReference<T> typeReference) throws JsonUtilException {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (JsonMappingException e) {
            throw new JsonUtilException("Error mapping JSON string to object", e);
        } catch (JsonProcessingException e) {
            throw new JsonUtilException("Error processing JSON string", e);
        }
    }
}

