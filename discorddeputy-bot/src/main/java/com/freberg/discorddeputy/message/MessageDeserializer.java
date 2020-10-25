package com.freberg.discorddeputy.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDeserializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Object deserialize(String json, MessageType messageType) {
        try {
            return objectMapper.readValue(json, messageType.getMessageClazz());
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message of type \"{}\", message \"{}\"", messageType, json, e);
            return null;
        }
    }
}
