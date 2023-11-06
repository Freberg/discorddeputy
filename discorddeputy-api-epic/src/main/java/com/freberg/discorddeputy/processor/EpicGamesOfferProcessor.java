package com.freberg.discorddeputy.processor;

import com.freberg.discorddeputy.json.epic.EpicGamesOffer;
import com.freberg.discorddeputy.message.Offer;
import com.freberg.discorddeputy.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
@Configuration
public class EpicGamesOfferProcessor {

    private final EpicGamesOfferMapper offerMapper;
    private final OfferRepository repository;

    @Bean
    public Function<Flux<EpicGamesOffer>, Flux<Offer>> offerProcessor() {
        return offers -> offers.map(offerMapper::mapMessage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filterWhen(offer -> repository.existsById(offer.getId()).map(b -> !b))
                .flatMap(this::persist);
    }

    private Mono<Offer> persist(Offer offer) {
        log.info("Persisted new offer with ID \"{}\" to DB", offer.getId());
        return repository.save(offer);
    }
}
