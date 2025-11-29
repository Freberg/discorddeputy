package com.freberg.discorddeputy;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscordNotificationTest {

    @Test
    public void keepUnknownFieldsAfterSerde() throws JacksonException {
        var objectMapper = new ObjectMapper();
        var json = String.format("{\"id\":\"%s\",\"source\":\"%s\",\"unknownField\":\"value\"}", "1", "some_source");
        var deserialized = objectMapper.readValue(json, DiscordNotification.class);
        var serialized = objectMapper.writeValueAsString(deserialized);
        assertEquals("1", deserialized.id());
        assertEquals("some_source", deserialized.source());
        assertEquals(json.length(), serialized.length());
    }
}