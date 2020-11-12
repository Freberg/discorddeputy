package com.freberg.discorddeputy.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.freberg.discorddeputy.model.News;
import com.freberg.discorddeputy.model.Offer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDeserializer {

    private final ObjectMapper objectMapper;

    public MessageDeserializer() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public Object deserialize(String json, MessageType messageType) {
        Class<?> messageClazz = switch (messageType) {
            case NEWS -> News.class;
            case OFFER -> Offer.class;
        };

        try {
            return objectMapper.readValue(json, messageClazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message of type \"{}\", message \"{}\"", messageType, json, e);
            return null;
        }
    }
}
