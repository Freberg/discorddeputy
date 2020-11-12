package com.freberg.discorddeputy.json.epic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesCurrencyInfo.class)
@JsonDeserialize(as = ImmutableEpicGamesCurrencyInfo.class)
public interface EpicGamesCurrencyInfo {

    int getDecimals();
}
