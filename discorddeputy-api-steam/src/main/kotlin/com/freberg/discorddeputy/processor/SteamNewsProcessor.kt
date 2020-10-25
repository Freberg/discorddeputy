package com.freberg.discorddeputy.processor

import com.freberg.discorddeputy.constant.DiscordDeputyConstants
import com.freberg.discorddeputy.message.MessageType
import com.freberg.discorddeputy.message.steam.SteamNews
import com.freberg.discorddeputy.repository.StreamNewsRepository
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.cloud.stream.messaging.Source
import org.springframework.integration.support.MessageBuilder
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@EnableBinding(Sink::class, Source::class)
class SteamNewsProcessor(val repository: StreamNewsRepository, val source: Source) {

    private val log = LoggerFactory.getLogger(javaClass)

    @StreamListener(Sink.INPUT)
    fun onSteamNews(news: SteamNews) {
        repository.existsById(news.id)
                .flatMap {
                    if (it) {
                        Mono.empty()
                    } else {
                        persist(news)
                                .then(dispatch(news))
                    }
                }.subscribe()
    }

    private fun persist(news: SteamNews): Mono<SteamNews> {
        log.info("Persisted new news with GID \"{}\" to DB", news.id)
        return repository.save(news)
    }

    private fun dispatch(news: SteamNews): Mono<SteamNews> {
        return Mono.fromCallable {
            source.output().send(MessageBuilder.withPayload(news)
                    .setHeader(DiscordDeputyConstants.MESSAGE_HEADER_MESSAGE_TYPE, MessageType.STEAM_NEWS)
                    .build())
            log.info("Put new news with GID \"{}\" to queue", news.id)
            news
        }.subscribeOn(Schedulers.elastic())
    }
}