package com.freberg.discorddeputy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@Data
@SuppressWarnings("unused")
public class DiscordNotification {

    private Map<String, Object> fields;
    private String id;
    private String source;

    @JsonCreator
    public DiscordNotification(Map<String, Object> fields) {
        this.fields = fields;
        this.id = (String) fields.get("id");
        this.source = (String) fields.get("source");
    }

    public DiscordNotification() {

    }

    @JsonValue
    public Map<String, Object> toMap() {
        return new HashMap<>(this.fields);
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }
}
