package com.freberg.discorddeputy.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesAppliedRules.class)
@JsonDeserialize(as = ImmutableEpicGamesAppliedRules.class)
public interface EpicGamesAppliedRules {

    String getEndDate();
}
