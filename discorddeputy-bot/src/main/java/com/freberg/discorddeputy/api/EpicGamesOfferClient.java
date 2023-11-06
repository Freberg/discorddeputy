package com.freberg.discorddeputy.api;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freberg.discorddeputy.message.Offer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class EpicGamesOfferClient {

    private static final String API_END_POINT_CURRENT = "/api/epicGames/currentOffers";
    private static final String API_END_POINT_UPCOMING = "/api/epicGames/upcomingOffers";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${discorddeputy.api.host.epicgames}")
    private String apiHost;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.create(apiHost);
    }

    public Mono<List<Offer>> getCurrentOffers() {
        return getCurrentOffers(API_END_POINT_CURRENT);
    }

    public Mono<List<Offer>> getUpcomingOffers() {
        return getCurrentOffers(API_END_POINT_UPCOMING);
    }

    private Mono<List<Offer>> getCurrentOffers(String endPoint) {
        return webClient.get()
                .uri(endPoint)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::deserialize);
    }

    private List<Offer> deserialize(String json) {
        try {
            List<Offer> offers = Optional
                    .of(objectMapper.readValue(json, new TypeReference<List<Offer>>() {
                    }))
                    .orElse(Collections.emptyList());

            log.info("Fetched {} offers from API", offers.size());
            return offers;
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message {}", json, e);
            return Collections.emptyList();
        }
    }
}
