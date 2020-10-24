package com.freberg.discorddeputy.fetcher;

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
                    .verify();
    }
}