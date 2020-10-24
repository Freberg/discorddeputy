package com.freberg.discorddeputy.message.epic;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.freberg.discorddeputy.json.DefaultInstantDeserializer;
import com.freberg.discorddeputy.json.DefaultInstantSerializer;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesPromotionalOffer.class)
@JsonDeserialize(as = ImmutableEpicGamesPromotionalOffer.class)
public interface EpicGamesPromotionalOffer {

    @JsonDeserialize(using = DefaultInstantDeserializer.class)
    @JsonSerialize(using = DefaultInstantSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss:SSSz", timezone = "UTC")
    Instant getStartDate();

    @JsonDeserialize(using = DefaultInstantDeserializer.class)
    @JsonSerialize(using = DefaultInstantSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss:SSSz", timezone = "UTC")
    Instant getEndDate();
}

