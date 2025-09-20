package com.freberg.discorddeputy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@SpringBootApplication
public class EpicGamesFetcherApplication {

    private static final Logger log = LoggerFactory.getLogger(EpicGamesFetcherApplication.class);
    private final EpicGamesOfferFetcher fetcher;

    public EpicGamesFetcherApplication(EpicGamesOfferFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Bean
    public Supplier<Flux<DiscordNotification>> offerSource() {
        return () -> fetcher.fetchOffers()
                .doOnNext(offer -> log.info("Put offer with ID \"{}\" to queue", offer.id()));
    }

    public static void main(String[] args) {
        SpringApplication.run(EpicGamesFetcherApplication.class, args);
    }
}
