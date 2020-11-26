package com.freberg.discorddeputy.fetcher;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freberg.discorddeputy.json.epic.EpicGamesOffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class EpicGamesOfferFetcher {

    private static final String EPIC_GAMES_HOST = "https://store-site-backend-static.ak.epicgames.com";
    private static final String EPIC_GAMES_URI = "/freeGamesPromotions";

    private final WebClient webClient = WebClient.create(EPIC_GAMES_HOST);
    private final ObjectMapper objectMapper;

    @Value("${epicGames.pollFrequency.duration:30}")
    private long pollFrequencyDuration;
    @Value("${epicGames.pollFrequency.timeUnit:MINUTES}")
    private ChronoUnit timeUnit;

    public EpicGamesOfferFetcher() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Flux<EpicGamesOffer> fetchOffers() {
        return Flux.interval(Duration.ZERO, Duration.of(pollFrequencyDuration, timeUnit))
                   .flatMap(timestamp -> retrieveOffers());
    }

    private Flux<EpicGamesOffer> retrieveOffers() {
        try {
            return webClient.get()
                            .uri(EPIC_GAMES_URI)
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .flatMap(response -> response.bodyToMono(String.class))
                            .map(this::deserialize)
                            .flatMapMany(Flux::fromIterable);
        } catch (Exception e) {
            log.error("Failed to retrieve epic games offers", e);
            return Flux.empty();
        }
    }

    private List<EpicGamesOffer> deserialize(String json) {
        try {
            List<EpicGamesOffer> offers = Optional.of(objectMapper.readValue(json, EpicGamesJsonResponse.class))
                                                  .map(EpicGamesJsonResponse::getData)
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

    private static class EpicGamesJsonResponse {
        @JsonProperty
        EpicGamesData data;

        EpicGamesData getData() {
            return data;
        }
    }

    private static class EpicGamesData {
        @JsonProperty("Catalog")
        EpicGameCatalog catalog;

        EpicGameCatalog getCatalog() {
            return catalog;
        }
    }

    private static class EpicGameCatalog {
        @JsonProperty
        EpicGamesSearchStore searchStore;

        EpicGamesSearchStore getSearchStore() {
            return searchStore;
        }
    }

    private static class EpicGamesSearchStore {
        @JsonProperty
        List<EpicGamesOffer> elements;

        List<EpicGamesOffer> getElements() {
            return elements;
        }
    }
}
