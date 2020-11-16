package com.freberg.discorddeputy.reponse;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import com.freberg.discorddeputy.constant.DiscordDeputyConstants;
import com.freberg.discorddeputy.model.ImageUrl;
import com.freberg.discorddeputy.model.Offer;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public class DiscordOfferResponseUtil {

    private static final String EPIC_GAMES_URL = "https://www.epicgames.com/store/en-US/free-games";

    private DiscordOfferResponseUtil() {
    }

    public static void createOfferMessage(Offer offer, EmbedCreateSpec embedCreateSpec,
                                          boolean isSmall) {
        embedCreateSpec.setTitle(createTitle(offer));
        embedCreateSpec.setUrl(EPIC_GAMES_URL);
        embedCreateSpec.setTimestamp(Instant.now());
        embedCreateSpec.setColor(Color.BLACK);

        if (isSmall) {
            resolveImageUrl(offer, DiscordDeputyConstants.IMAGE_TYPE_THUMBNAIL)
                    .ifPresent(embedCreateSpec::setThumbnail);
        } else {
            resolveImageUrl(offer, DiscordDeputyConstants.IMAGE_TYPE_OFFERING_WIDE)
                    .ifPresent(embedCreateSpec::setImage);
        }

        embedCreateSpec.addField(offer.getTitle(), createDescription(offer), false);
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

    private static Optional<String> resolveImageUrl(Offer offer, String type) {
        return Optional.of(offer)
                       .map(Offer::getImageUrls)
                       .stream()
                       .flatMap(Collection::stream)
                       .filter(image -> type.equals(image.getDescription()))
                       .findFirst()
                       .map(ImageUrl::getUrl);
    }
}
