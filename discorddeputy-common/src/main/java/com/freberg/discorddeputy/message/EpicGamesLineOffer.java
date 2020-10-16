package com.freberg.discorddeputy.message;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesLineOffer.class)
@JsonDeserialize(as = ImmutableEpicGamesLineOffer.class)
public interface EpicGamesLineOffer {

    List<EpicGamesAppliedRules> getAppliedRules();
}
