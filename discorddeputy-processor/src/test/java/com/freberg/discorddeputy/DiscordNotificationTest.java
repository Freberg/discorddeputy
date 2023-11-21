package com.freberg.discorddeputy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscordNotificationTest {

    @Test
    public void keepUnknownFieldsAfterSerde() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var json = String.format("{\"id\":\"%s\",\"source\":\"%s\",\"unknownField\":\"value\"}", "1", "some_source");
        var deserialized = objectMapper.readValue(json, DiscordNotification.class);
        var serialized = objectMapper.writeValueAsString(deserialized);
        assertEquals("1", deserialized.getId());
        assertEquals("some_source", deserialized.getSource());
        assertEquals(json.length(), serialized.length());
    }
}