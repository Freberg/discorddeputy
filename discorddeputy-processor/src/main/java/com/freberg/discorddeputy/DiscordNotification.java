package com.freberg.discorddeputy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column; // Import Column
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;

@Table("discord_notifications")
@SuppressWarnings("unused")
public record DiscordNotification(@Column("data") Map<String, Object> fields, @Id String id, String source) {

    @JsonCreator
    public DiscordNotification(Map<String, Object> fields) {
        this(fields, (String) fields.get("id"), (String) fields.get("source"));
    }

    @JsonValue
    public Map<String, Object> toMap() {
        return new HashMap<>(this.fields);
    }
}
