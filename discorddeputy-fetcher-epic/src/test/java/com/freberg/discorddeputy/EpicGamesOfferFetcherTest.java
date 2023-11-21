package com.freberg.discorddeputy;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

class EpicGamesOfferFetcherTest {

    @Test
    void verifyOffersNonEmpty() {
        var fetcher = new EpicGamesOfferFetcher(1, ChronoUnit.SECONDS);
        var stepVerifier = StepVerifier.create(fetcher.fetchOffers());

        for (int i = 0; i < 5; i++) {
            stepVerifier.expectNextMatches(notification -> {
                System.out.println(notification);
                return Objects.nonNull(notification);
            });
        }

        stepVerifier.expectNextMatches(Objects::nonNull)
                .thenCancel()
                .verify(Duration.of(10, ChronoUnit.SECONDS));
    }
}