package com.freberg.discorddeputy.message.epic;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesPromotions.class)
@JsonDeserialize(as = ImmutableEpicGamesPromotions.class)
public interface EpicGamesPromotions {

    List<EpicGamesPromotionalOffers> getPromotionalOffers();

    List<EpicGamesPromotionalOffers> getUpcomingPromotionalOffers();
}
