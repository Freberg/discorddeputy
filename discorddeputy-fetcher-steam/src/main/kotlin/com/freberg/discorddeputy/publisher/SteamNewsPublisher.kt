package com.freberg.discorddeputy.publisher

import com.freberg.discorddeputy.fetcher.SteamNewsFetcher
import com.freberg.discorddeputy.json.steam.SteamNews
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.function.Supplier


@Component
@Configuration
class SteamNewsSource(val fetcher: SteamNewsFetcher) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun newsSource(): Supplier<Flux<SteamNews>> =
            Supplier {
                fetcher.fetchNews()
                        .doOnNext { log.info("Put news with GID \"{}\" to queue", it.gid) }
            }
}

