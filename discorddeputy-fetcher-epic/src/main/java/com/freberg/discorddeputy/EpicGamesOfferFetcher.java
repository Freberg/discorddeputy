package com.freberg.discorddeputy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.freberg.discorddeputy.EpicGamesApiResponse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.freberg.discorddeputy.DiscordNotification.Type.CURRENT_OFFER;
import static com.freberg.discorddeputy.DiscordNotification.Type.UPCOMING_OFFER;

@Component
public class EpicGamesOfferFetcher {

    private static final Logger log = LoggerFactory.getLogger(EpicGamesOfferFetcher.class);

    private static final String EPIC_GAMES_URL = "https://www.epicgames.com/store/en-US/free-games";
    private static final String EPIC_GAMES_API_URL = "https://store-site-backend-static.ak.epicgames.com";
    private static final String EPIC_GAMES_URI = "/freeGamesPromotions";
    private static final String IMAGE_TYPE_OFFERING_WIDE = "OfferImageWide";
    private static final String IMAGE_TYPE_THUMBNAIL = "Thumbnail";
    private static final String IMAGE_TYPE_DIESEL_STORE_FRONT_WIDE = "DieselStoreFrontWide";
    private static final String IMAGE_TYPE_VAULT_CLOSED = "VaultClosed";

    private final WebClient webClient = WebClient.create();
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .addModule(new JavaTimeModule())
            .build();
    private final Duration pollDuration;

    public EpicGamesOfferFetcher(@Value("${epicGames.pollFrequency.duration:30}") long pollFrequencyDuration,
                                 @Value("${epicGames.pollFrequency.timeUnit:MINUTES}") ChronoUnit timeUnit) {
        this.pollDuration = Duration.of(pollFrequencyDuration, timeUnit);
    }

    public Flux<DiscordNotification> fetchOffers() {
        return Flux.interval(Duration.ZERO, pollDuration)
                .flatMap(timestamp -> retrieveOffers());
    }

    private Flux<DiscordNotification> retrieveOffers() {
        try {
            return webClient.get()
                    .uri(EPIC_GAMES_API_URL + EPIC_GAMES_URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(throwable -> {
                        log.error("Failed to fetch data from epic games", throwable);
                        return Mono.empty();
                    })
                    .flatMapIterable(this::deserialize)
                    .flatMapIterable(this::toDiscordNotifications);
        } catch (Exception e) {
            log.error("Failed to retrieve epic games offers", e);
            return Flux.empty();
        }
    }

    private List<EpicGamesOffer> deserialize(String json) {
        try {
            List<EpicGamesOffer> offers = Optional.of(objectMapper.readValue(json, EpicGamesApiResponse.class))
                    .map(EpicGamesApiResponse::getData)
                    .map(EpicGamesData::getCatalog)
                    .map(EpicGameCatalog::getSearchStore)
                    .map(EpicGamesSearchStore::getElements)
                    .orElse(Collections.emptyList());

            log.info("Fetched {} offers from epic games", offers.size());
            return offers;
        } catch (Exception e) {
            log.error("Failed to deserialize message {}", json, e);
            return Collections.emptyList();
        }
    }

    private Collection<DiscordNotification> toDiscordNotifications(EpicGamesOffer offer) {
        return Optional.of(offer)
                .map(EpicGamesOffer::getPromotions)
                .stream()
                .flatMap(promotions -> Stream.of(promotions.getPromotionalOffers(),
                        promotions.getUpcomingPromotionalOffers()))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(EpicGamesPromotionalOffers::getPromotionalOffers)
                .flatMap(Collection::stream)
                .map(promotion -> toDiscordNotification(offer, promotion))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<DiscordNotification> toDiscordNotification(EpicGamesOffer offer,
                                                                EpicGamesPromotionalOffer promotionalOffer) {
        if (promotionalOffer.getStartDate() == null || promotionalOffer.getEndDate() == null) {
            return Optional.empty();
        }
        if (Instant.now().isAfter(promotionalOffer.getEndDate())) {
            return Optional.empty();
        }

        var type = Instant.now().isAfter(promotionalOffer.getStartDate()) ? CURRENT_OFFER : UPCOMING_OFFER;
        var builder = ImmutableDiscordNotification.builder()
                .id(offer.getId() + "_" + type)
                .type(type)
                .timestamp(Instant.now())
                .url(EPIC_GAMES_URL)
                .title(String.format("Epic Games - %s!", type.displayName))
                .descriptionHeader(offer.getTitle())
                .startTime(promotionalOffer.getStartDate())
                .endTime(promotionalOffer.getEndDate())
                .description("From: " + promotionalOffer.getStartDate() + "\n" +
                        "To: " + promotionalOffer.getEndDate());

        resolveImageUrl(offer, IMAGE_TYPE_OFFERING_WIDE, IMAGE_TYPE_DIESEL_STORE_FRONT_WIDE, IMAGE_TYPE_VAULT_CLOSED)
                .ifPresent(builder::imageUrl);

        resolveImageUrl(offer, IMAGE_TYPE_THUMBNAIL)
                .ifPresent(builder::thumbNailUrl);

        return Optional.of(builder.build());
    }

    private static Optional<String> resolveImageUrl(EpicGamesOffer offer, String... acceptedTypes) {
        return Arrays.stream(acceptedTypes)
                .map(type ->
                        Optional.of(offer)
                                .map(EpicGamesOffer::getKeyImages)
                                .stream()
                                .flatMap(Collection::stream)
                                .filter(image -> type.equals(image.getType()))
                                .findFirst()
                                .map(EpicGamesImage::getUrl)
                )
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity());
    }
}
