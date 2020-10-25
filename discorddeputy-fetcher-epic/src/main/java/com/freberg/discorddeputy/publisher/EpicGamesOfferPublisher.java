package com.freberg.discorddeputy.publisher;

import com.freberg.discorddeputy.constant.DiscordDeputyConstants;
import com.freberg.discorddeputy.fetcher.EpicGamesOfferFetcher;
import com.freberg.discorddeputy.message.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@EnableBinding(Source.class)
@RequiredArgsConstructor
public class EpicGamesOfferPublisher implements ApplicationRunner {

    private final EpicGamesOfferFetcher fetcher;
    private final Source source;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        fetcher.fetchOffers()
               .flatMap(offer -> Mono.fromCallable(() -> {
                   source.output().send(MessageBuilder.withPayload(offer)
                                                      .setHeader(DiscordDeputyConstants.MESSAGE_HEADER_MESSAGE_TYPE,
                                                              MessageType.EPIC_GAMES_OFFER)
                                                      .build());
                   log.info("Put offer with ID \"{}\" to queue", offer.getId());
                   return offer;
               }).subscribeOn(Schedulers.boundedElastic()))
               .subscribe();
    }
}
