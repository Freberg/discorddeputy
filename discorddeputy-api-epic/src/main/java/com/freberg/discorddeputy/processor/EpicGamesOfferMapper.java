package com.freberg.discorddeputy.processor;

import com.freberg.discorddeputy.constant.DiscordDeputyConstants;
import com.freberg.discorddeputy.json.epic.EpicGamesOffer;
import com.freberg.discorddeputy.json.epic.EpicGamesPromotionalOffer;
import com.freberg.discorddeputy.json.epic.EpicGamesPromotionalOffers;
import com.freberg.discorddeputy.json.epic.EpicGamesPromotions;
import com.freberg.discorddeputy.message.ImageUrl;
import com.freberg.discorddeputy.message.ImmutableImageUrl;
import com.freberg.discorddeputy.message.ImmutableOffer;
import com.freberg.discorddeputy.message.Offer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EpicGamesOfferMapper {

    private static final String EPIC_GAMES_URL = "https://www.epicgames.com/store/en-US/free-games";

    private enum OfferStatus {CURRENT, UPCOMING, UNKNOWN}

    public Optional<Offer> mapMessage(EpicGamesOffer inputMessage) {
        try {
            return Optional.of(
                    ImmutableOffer.builder()
                            .sourceTimestamp(Instant.now())
                            .processTimestamp(Instant.now())
                            .source(DiscordDeputyConstants.SOURCE_EPIC_GAMES)
                            .title(inputMessage.getTitle())
                            .id(getId(inputMessage))
                            .description(inputMessage.getDescription())
                            .url(EPIC_GAMES_URL)
                            .startTime(getStartDate(inputMessage))
                            .endTime(getEndDate(inputMessage))
                            .imageUrls(extractImageUrls(inputMessage))
                            .build()
            );
        } catch (RuntimeException e) {
            log.error("Failed to map offer \"{}\"", inputMessage, e);
            return Optional.empty();
        }
    }

    private Collection<ImageUrl> extractImageUrls(EpicGamesOffer inputMessage) {
        return inputMessage.getKeyImages().stream()
                .map(image -> ImmutableImageUrl.builder()
                        .url(image.getUrl())
                        .description(image.getType())
                        .build())
                .collect(Collectors.toList());
    }

    private String getId(EpicGamesOffer epicGamesOffer) {
        return epicGamesOffer.getId() + "-" + getOfferStatus(epicGamesOffer);
    }

    private OfferStatus getOfferStatus(EpicGamesOffer epicGamesOffer) {
        Instant startTime = getStartDate(epicGamesOffer);
        if (startTime == null) {
            return OfferStatus.UNKNOWN;
        } else if (Instant.now().isBefore(getStartDate(epicGamesOffer))) {
            return OfferStatus.UPCOMING;
        } else {
            return OfferStatus.CURRENT;
        }
    }

    private Instant getStartDate(EpicGamesOffer epicGamesOffer) {
        return Optional.ofNullable(getCurrentTimeLimit(epicGamesOffer, EpicGamesPromotionalOffer::getStartDate))
                .orElse(getUpcomingTimeLimit(epicGamesOffer, EpicGamesPromotionalOffer::getStartDate));
    }

    private Instant getEndDate(EpicGamesOffer epicGamesOffer) {
        return Optional.ofNullable(getCurrentTimeLimit(epicGamesOffer, EpicGamesPromotionalOffer::getEndDate))
                .orElse(getUpcomingTimeLimit(epicGamesOffer, EpicGamesPromotionalOffer::getEndDate));
    }

    private Instant getCurrentTimeLimit(EpicGamesOffer epicGamesOffer,
                                        Function<EpicGamesPromotionalOffer, Instant> timeLimitGetter) {
        return getTimeLimit(epicGamesOffer, timeLimitGetter, EpicGamesPromotions::getPromotionalOffers);
    }

    private Instant getUpcomingTimeLimit(EpicGamesOffer epicGamesOffer,
                                         Function<EpicGamesPromotionalOffer, Instant> timeLimitGetter) {
        return getTimeLimit(epicGamesOffer, timeLimitGetter,
                EpicGamesPromotions::getUpcomingPromotionalOffers);
    }

    private Instant getTimeLimit(EpicGamesOffer epicGamesOffer,
                                 Function<EpicGamesPromotionalOffer, Instant> timeLimitGetter,
                                 Function<EpicGamesPromotions, List<EpicGamesPromotionalOffers>> offerGetter) {
        return Optional.ofNullable(epicGamesOffer)
                .map(EpicGamesOffer::getPromotions)
                .map(offerGetter)
                .stream()
                .flatMap(Collection::stream)
                .map(EpicGamesPromotionalOffers::getPromotionalOffers)
                .flatMap(Collection::stream)
                .map(timeLimitGetter)
                .findFirst()
                .orElse(null);
    }
}
