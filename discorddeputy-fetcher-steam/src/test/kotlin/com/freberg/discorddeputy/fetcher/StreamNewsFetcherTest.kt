package com.freberg.discorddeputy.fetcher

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import java.util.*

@SpringBootTest
class StreamNewsFetcherTest {

    @Autowired
    lateinit var fetcher: StreamNewsFetcher;

    @Test
    fun verifyNewsNonEmpty() {
        StepVerifier.create(fetcher.fetchNews())
                .expectNextMatches { Objects.nonNull(it) }
                .thenCancel()
                .verify()
    }
}