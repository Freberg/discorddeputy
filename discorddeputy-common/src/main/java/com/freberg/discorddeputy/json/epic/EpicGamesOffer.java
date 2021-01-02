package com.freberg.discorddeputy.json.epic;

import javax.annotation.Nullable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesOffer.class)
@JsonDeserialize(as = ImmutableEpicGamesOffer.class)
public interface EpicGamesOffer {

    String getTitle();

    String getId();

    String getDescription();

    EpicGamesPrice getPrice();

    List<EpicGamesImage> getKeyImages();

    @Nullable
    EpicGamesPromotions getPromotions();
}
