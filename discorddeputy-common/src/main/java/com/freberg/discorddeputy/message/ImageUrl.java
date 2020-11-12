package com.freberg.discorddeputy.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableImageUrl.class)
@JsonDeserialize(as = ImmutableImageUrl.class)
public interface ImageUrl {

    String getUrl();

    String getDescription();
}
