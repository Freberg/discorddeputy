package com.freberg.discorddeputy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public record DiscordNotification(Map<String, Object> fields, String id, String source) {

    @JsonCreator
    public DiscordNotification(Map<String, Object> fields) {
        this(fields, (String) fields.get("id"), (String) fields.get("source"));
    }

    @JsonValue
    public Map<String, Object> toMap() {
        return new HashMap<>(this.fields);
    }
}
