package com.freberg.discorddeputy.publisher

import com.freberg.discorddeputy.constant.DiscordDeputyConstants
import com.freberg.discorddeputy.fetcher.StreamNewsFetcher
import com.freberg.discorddeputy.message.MessageType
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.integration.support.MessageBuilder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers


@Component
@EnableBinding(Source::class)
class StreamNewsPublisher(val fetcher: StreamNewsFetcher, val source: Source) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass);

    override fun run(args: ApplicationArguments?) {
        fetcher.fetchNews()
                .flatMap {
                    Mono.fromCallable {
                        source.output().send(MessageBuilder.withPayload(it)
                                .setHeader(DiscordDeputyConstants.MESSAGE_HEADER_MESSAGE_TYPE, MessageType.NEWS)
                                .build())
                        log.info("Put news with GID \"{}\" to queue", it.gid)
                        it
                    }
                }
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe()
    }
}

