package com.freberg.discorddeputy.controller;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.freberg.discorddeputy.message.EpicGamesOffer;
import com.freberg.discorddeputy.message.EpicGamesPromotionalOffer;
import com.freberg.discorddeputy.message.EpicGamesPromotionalOffers;
import com.freberg.discorddeputy.message.EpicGamesPromotions;
import com.freberg.discorddeputy.repository.EpicGamesOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/epicGames")
@RequiredArgsConstructor
public class EpicGamesOfferController {

    private final EpicGamesOfferRepository repository;

    @GetMapping("/currentOffers")
    public Flux<EpicGamesOffer> getCurrentOffers() {
        return repository.findAll()
                         .filter(offer -> isAfter(offer, this::getStartDate))
                         .filter(offer -> isBefore(offer, this::getEndDate));
    }

    @GetMapping("/upcomingOffers")
    public Flux<EpicGamesOffer> getUpcomingOffers() {
        return repository.findAll()
                         .filter(offer -> isBefore(offer, this::getStartDate));
    }

    @GetMapping("/allOffers")
    public Flux<EpicGamesOffer> getAllOffers() {
        return repository.findAll();
    }

    private boolean isBefore(EpicGamesOffer offer, Function<EpicGamesOffer, Instant> timeLimitGetter) {
        return Optional.ofNullable(timeLimitGetter.apply(offer))
                       .map(limit -> Instant.now().isBefore(limit))
                       .orElse(false);
    }

    private boolean isAfter(EpicGamesOffer offer, Function<EpicGamesOffer, Instant> timeLimitGetter) {
        return Optional.ofNullable(timeLimitGetter.apply(offer))
                       .map(limit -> Instant.now().isAfter(limit))
                       .orElse(false);
    }

    private Instant getStartDate(EpicGamesOffer epicGamesOffer) {
        return Optional.ofNullable(getCurrentTimeLimit(epicGamesOffer, EpicGamesPromotionalOffer::getStartDate))
                       .orElse(getUpcomingTimeLimit(epicGamesOffer, EpicGamesPromotionalOffer::getStartDate));
    }

    private Instant getEndDate(EpicGamesOffer epicGamesOffer) {
        return Optional.ofNullable(getCurrentTimeLimit(epicGamesOffer, EpicGamesPromotionalOffer::getStartDate))
                       .orElse(getUpcomingTimeLimit(epicGamesOffer, EpicGamesPromotionalOffer::getEndDate));
    }

    private Instant getCurrentTimeLimit(EpicGamesOffer epicGamesOffer,
                                        Function<EpicGamesPromotionalOffer, Instant> timeLimitGetter) {
        return getTimeLimit(epicGamesOffer, timeLimitGetter, EpicGamesPromotions::getPromotionalOffers);
    }

    private Instant getUpcomingTimeLimit(EpicGamesOffer epicGamesOffer,
                                         Function<EpicGamesPromotionalOffer, Instant> timeLimitGetter) {
        return getTimeLimit(epicGamesOffer, timeLimitGetter,
                EpicGamesPromotions::getUpcomingPromotionalOffers);
    }

    private Instant getTimeLimit(EpicGamesOffer epicGamesOffer,
                                 Function<EpicGamesPromotionalOffer, Instant> timeLimitGetter,
                                 Function<EpicGamesPromotions, List<EpicGamesPromotionalOffers>> offerGetter) {
        return Optional.ofNullable(epicGamesOffer)
                       .map(EpicGamesOffer::getPromotions)
                       .map(offerGetter)
                       .stream()
                       .flatMap(Collection::stream)
                       .map(EpicGamesPromotionalOffers::getPromotionalOffers)
                       .flatMap(Collection::stream)
                       .map(timeLimitGetter)
                       .findFirst()
                       .orElse(null);
    }
}
