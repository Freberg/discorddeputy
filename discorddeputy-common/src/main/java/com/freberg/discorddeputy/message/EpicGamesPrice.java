package com.freberg.discorddeputy.message;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesPrice.class)
@JsonDeserialize(as = ImmutableEpicGamesPrice.class)
public interface EpicGamesPrice {

    EpicGamesTotalPrice getTotalPrice();

    List<EpicGamesLineOffer> getLineOffers();
}
