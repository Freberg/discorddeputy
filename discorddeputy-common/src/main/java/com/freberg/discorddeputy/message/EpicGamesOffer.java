package com.freberg.discorddeputy.message;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.immutables.value.internal.$processor$.meta.$CriteriaMirrors;

@Value.Immutable
@$CriteriaMirrors.Criteria
@$CriteriaMirrors.CriteriaRepository
@JsonSerialize(as = ImmutableEpicGamesOffer.class)
@JsonDeserialize(as = ImmutableEpicGamesOffer.class)
public interface EpicGamesOffer {

    String getTitle();

    @$CriteriaMirrors.CriteriaId
    String getId();

    String getDescription();

    EpicGamesPrice getPrice();

    List<EpicGamesImage> getKeyImages();
}
