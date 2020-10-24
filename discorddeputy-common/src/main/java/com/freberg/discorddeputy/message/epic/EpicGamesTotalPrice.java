package com.freberg.discorddeputy.message.epic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEpicGamesTotalPrice.class)
@JsonDeserialize(as = ImmutableEpicGamesTotalPrice.class)
public interface EpicGamesTotalPrice {

    int getDiscountPrice();

    int getOriginalPrice();

    int getDiscount();

    String getCurrencyCode();

    EpicGamesCurrencyInfo getCurrencyInfo();
}
