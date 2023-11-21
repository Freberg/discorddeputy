package com.freberg.discorddeputy

import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

class SteamNewsFetcherTest {

    @Test
    fun verifyNewsNonEmpty() {
        val fetcher = SteamNewsFetcher(1, ChronoUnit.SECONDS, listOf("594650"))
        val stepVerifier = StepVerifier.create(fetcher.fetchNews())

        for (i in 0..4) {
            stepVerifier.expectNextMatches { notification: DiscordNotification? ->
                println(notification)
                Objects.nonNull(notification)
            }
        }

        stepVerifier.expectNextMatches { obj: DiscordNotification? ->
            Objects.nonNull(
                obj
            )
        }
            .thenCancel()
            .verify(Duration.of(10, ChronoUnit.SECONDS))
    }
}