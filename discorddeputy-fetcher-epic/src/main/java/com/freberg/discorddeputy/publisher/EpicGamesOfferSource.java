package com.freberg.discorddeputy.publisher;

import com.freberg.discorddeputy.fetcher.EpicGamesOfferFetcher;
import com.freberg.discorddeputy.json.epic.EpicGamesOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@Slf4j
@Component
@Configuration
@RequiredArgsConstructor
public class EpicGamesOfferSource {

    private final EpicGamesOfferFetcher fetcher;

    @Bean
    public Supplier<Flux<EpicGamesOffer>> offerSource() {
        return () -> fetcher.fetchOffers()
                .doOnNext(offer -> log.info("Put offer with ID \"{}\" to queue", offer.getId()));
    }
}
