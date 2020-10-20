package com.freberg.discorddeputy.message;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesPromotionalOffers.class)
@JsonDeserialize(as = ImmutableEpicGamesPromotionalOffers.class)
public interface EpicGamesPromotionalOffers {

    List<EpicGamesPromotionalOffer> getPromotionalOffers();
}
