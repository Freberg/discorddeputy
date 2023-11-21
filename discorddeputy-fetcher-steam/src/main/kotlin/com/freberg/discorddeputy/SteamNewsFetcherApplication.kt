package com.freberg.discorddeputy

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import reactor.core.publisher.Flux
import java.util.function.Supplier

@SpringBootApplication
@Suppress("unused")
class DiscordDeputySteamFetcherApplication(val fetcher: SteamNewsFetcher) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun newsSource(): Supplier<Flux<DiscordNotification>> =
        Supplier {
            fetcher.fetchNews()
                .doOnNext { log.info("Put news with GID \"{}\" to queue", it.id) }
        }
}

fun main(args: Array<String>) {
    runApplication<DiscordDeputySteamFetcherApplication>(*args)
}

