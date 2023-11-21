package com.freberg.discorddeputy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;

class EpicGamesApiResponse {
    @JsonProperty
    EpicGamesData data;

    EpicGamesData getData() {
        return data;
    }


    static class EpicGamesData {
        @JsonProperty("Catalog")
        EpicGameCatalog catalog;

        EpicGameCatalog getCatalog() {
            return catalog;
        }
    }

    static class EpicGameCatalog {
        @JsonProperty
        EpicGamesSearchStore searchStore;

        EpicGamesSearchStore getSearchStore() {
            return searchStore;
        }
    }

    static class EpicGamesSearchStore {
        @JsonProperty
        List<EpicGamesOffer> elements;

        List<EpicGamesOffer> getElements() {
            return elements;
        }
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesOffer.class)
    interface EpicGamesOffer {

        String getTitle();

        String getId();

        String getDescription();

        EpicGamesPrice getPrice();

        List<EpicGamesImage> getKeyImages();

        @Nullable
        EpicGamesPromotions getPromotions();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesPrice.class)
    interface EpicGamesPrice {

        EpicGamesTotalPrice getTotalPrice();

        List<EpicGamesLineOffer> getLineOffers();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesTotalPrice.class)
    interface EpicGamesTotalPrice {

        int getDiscountPrice();

        int getOriginalPrice();

        int getDiscount();

        String getCurrencyCode();

        EpicGamesCurrencyInfo getCurrencyInfo();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesCurrencyInfo.class)
    interface EpicGamesCurrencyInfo {

        int getDecimals();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesLineOffer.class)
    interface EpicGamesLineOffer {

        List<EpicGamesAppliedRules> getAppliedRules();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesAppliedRules.class)
    interface EpicGamesAppliedRules {

        String getEndDate();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesImage.class)
    interface EpicGamesImage {

        String getType();

        String getUrl();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesPromotions.class)
    interface EpicGamesPromotions {

        List<EpicGamesPromotionalOffers> getPromotionalOffers();

        List<EpicGamesPromotionalOffers> getUpcomingPromotionalOffers();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesPromotionalOffers.class)
    interface EpicGamesPromotionalOffers {

        List<EpicGamesPromotionalOffer> getPromotionalOffers();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEpicGamesPromotionalOffer.class)
    interface EpicGamesPromotionalOffer {

        @Nullable
        Instant getStartDate();

        @Nullable
        Instant getEndDate();
    }
}
