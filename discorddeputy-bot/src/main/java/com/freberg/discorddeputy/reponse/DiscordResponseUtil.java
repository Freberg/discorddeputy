package com.freberg.discorddeputy.reponse;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import com.freberg.discorddeputy.constant.DiscordDeputyConstants;
import com.freberg.discorddeputy.message.epic.EpicGamesImage;
import com.freberg.discorddeputy.message.epic.EpicGamesOffer;
import com.freberg.discorddeputy.message.epic.EpicGamesPrice;
import com.freberg.discorddeputy.message.epic.EpicGamesTotalPrice;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public class DiscordResponseUtil {

    private static final String EPIC_GAMES_URL = "https://www.epicgames.com/store/en-US/free-games";

    private DiscordResponseUtil() {
    }

    public static void createEpicGamesOfferMessage(EpicGamesOffer offer, EmbedCreateSpec embedCreateSpec,
                                                   boolean isSmall) {
        embedCreateSpec.setTitle("Epic Games - Current Deal!");
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

        embedCreateSpec.addField(offer.getTitle(), generateDescription(offer), false);
    }

    private static String generateDescription(EpicGamesOffer offer) {
        StringBuilder stringBuilder = new StringBuilder();

        generateDiscountDescription(offer)
                .ifPresent(discountDescription -> {
                    stringBuilder.append(discountDescription);
                    stringBuilder.append("\n");
                });

        return stringBuilder.toString();
    }

    private static Optional<String> generateDiscountDescription(EpicGamesOffer offer) {
        return Optional.of(offer)
                       .map(EpicGamesOffer::getPrice)
                       .map(EpicGamesPrice::getTotalPrice)
                       .map(totalPrice -> DiscordResponseUtil.getPrice(totalPrice.getOriginalPrice(), totalPrice)
                                          + " -> " +
                                          DiscordResponseUtil.getPrice(totalPrice.getDiscountPrice(), totalPrice));
    }

    private static String getPrice(int price, EpicGamesTotalPrice totalPrice) {
        return new DecimalFormat("0.00").format(price / Math.pow(10, totalPrice.getCurrencyInfo()
                                                                               .getDecimals())) + " " +
                                                totalPrice.getCurrencyCode();
    }

    private static Optional<String> resolveImageUrl(EpicGamesOffer offer, String type) {
        return Optional.of(offer)
                       .map(EpicGamesOffer::getKeyImages)
                       .stream()
                       .flatMap(Collection::stream)
                       .filter(image -> type.equals(image.getType()))
                       .findFirst()
                       .map(EpicGamesImage::getUrl);
    }
}
