package com.freberg.discorddeputy.processor

import com.freberg.discorddeputy.json.steam.SteamNews
import com.freberg.discorddeputy.message.News
import com.freberg.discorddeputy.repository.NewsRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function

@Component
@Configuration
class SteamNewsProcessor(private val newsMapper: SteamNewsMapper, private val repository: NewsRepository) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun newsProcessor(): Function<Flux<SteamNews>, Flux<News>> {
        return Function { steamNews ->
            steamNews
                    .mapNotNull { newsMapper.mapMessage(it) }
                    .map { it!! }
                    .filterWhen { repository.existsById(it.id).map { b -> !b } }
                    .flatMap { persist(it) }
        }
    }

    private fun persist(news: News): Mono<News> {
        log.info("Persisted new news with GID \"{}\" to DB", news.id)
        return repository.save(news)
    }
}