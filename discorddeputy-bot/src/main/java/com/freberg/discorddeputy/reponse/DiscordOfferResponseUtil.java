package com.freberg.discorddeputy.reponse;

import com.freberg.discorddeputy.constant.DiscordDeputyConstants;
import com.freberg.discorddeputy.model.ImageUrl;
import com.freberg.discorddeputy.model.Offer;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class DiscordOfferResponseUtil {

    private static final String EPIC_GAMES_URL = "https://www.epicgames.com/store/en-US/free-games";

    private DiscordOfferResponseUtil() {
    }

    public static EmbedCreateSpec createOfferMessage(Offer offer, boolean isSmall) {
        var builder = EmbedCreateSpec.builder()
                .title(createTitle(offer))
                .url(EPIC_GAMES_URL)
                .timestamp(Instant.now())
                .color(Color.BLACK);

        if (isSmall) {
            resolveImageUrl(offer, DiscordDeputyConstants.IMAGE_TYPE_THUMBNAIL)
                    .ifPresent(builder::thumbnail);
        } else {
            resolveImageUrl(offer,
                    List.of(DiscordDeputyConstants.IMAGE_TYPE_OFFERING_WIDE,
                            DiscordDeputyConstants.IMAGE_TYPE_DIESEL_STORE_FRONT_WIDE,
                            DiscordDeputyConstants.IMAGE_TYPE_VAULT_CLOSED))
                    .ifPresent(builder::image);
        }

        builder.addField(offer.getTitle(), createDescription(offer), false);
        return builder.build();
    }

    private static String createTitle(Offer offer) {
        if (Instant.now().isBefore(offer.getStartTime().toInstant())) {
            return "Epic Games - Upcoming Offer!";
        } else {
            return "Epic Games - Current Offer!";
        }
    }

    private static String createDescription(Offer offer) {
        return "From: " + offer.getStartTime() + "\n" +
                "To: " + offer.getEndTime();
    }

    private static Optional<String> resolveImageUrl(Offer offer, String acceptedType) {
        return resolveImageUrl(offer, List.of(acceptedType));
    }

    private static Optional<String> resolveImageUrl(Offer offer, List<String> acceptedTypes) {
        return acceptedTypes.stream()
                .map(type ->
                        Optional.of(offer)
                                .map(Offer::getImageUrls)
                                .stream()
                                .flatMap(Collection::stream)
                                .filter(image -> type.equals(image.getDescription()))
                                .findFirst()
                                .map(ImageUrl::getUrl)
                )
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity());
    }
}
