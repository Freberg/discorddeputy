package com.freberg.discorddeputy.json.steam;

import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.freberg.discorddeputy.json.DefaultTimestampDeserializer;
import com.freberg.discorddeputy.json.DefaultTimestampSerializer;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSteamNews.class)
@JsonDeserialize(as = ImmutableSteamNews.class)
public interface SteamNews {

    String getGid();

    String getTitle();

    String getContents();

    String getUrl();

    String getFeedLabel();

    String getFeedName();

    @JsonSerialize(using = DefaultTimestampSerializer.class)
    @JsonDeserialize(using = DefaultTimestampDeserializer.class)
    Instant getDate();

    String getAppId();
}
