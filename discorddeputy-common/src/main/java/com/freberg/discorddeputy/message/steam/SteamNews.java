package com.freberg.discorddeputy.message.steam;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.freberg.discorddeputy.json.DefaultTimestampDeserializer;
import com.freberg.discorddeputy.json.DefaultTimestampSerializer;
import org.immutables.value.Value;
import org.immutables.value.internal.$processor$.meta.$CriteriaMirrors;

@Value.Immutable
@$CriteriaMirrors.Criteria
@$CriteriaMirrors.CriteriaRepository
@JsonSerialize(as = ImmutableSteamNews.class)
@JsonDeserialize(as = ImmutableSteamNews.class)
public interface SteamNews {

    @JsonAlias("gid")
    @$CriteriaMirrors.CriteriaId
    String getId();

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
