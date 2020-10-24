package com.freberg.discorddeputy.message.epic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesImage.class)
@JsonDeserialize(as = ImmutableEpicGamesImage.class)
public interface EpicGamesImage {

    String getType();

    String getUrl();
}
