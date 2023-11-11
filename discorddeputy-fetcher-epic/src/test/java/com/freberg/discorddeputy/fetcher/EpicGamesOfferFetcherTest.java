package com.freberg.discorddeputy.fetcher;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class EpicGamesOfferFetcherTest {

    @Autowired
    private EpicGamesOfferFetcher epicGamesOfferFetcher;

    @Test
    void verifyOffersNonEmpty() {
        StepVerifier.create(epicGamesOfferFetcher.fetchOffers())
                    .expectNextMatches(Objects::nonNull)
                    .thenCancel()
                    .verify(Duration.of(10, ChronoUnit.SECONDS));
    }
}