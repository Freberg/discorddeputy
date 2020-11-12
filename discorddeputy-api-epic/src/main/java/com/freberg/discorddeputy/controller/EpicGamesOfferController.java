package com.freberg.discorddeputy.controller;

import java.time.Instant;

import com.freberg.discorddeputy.message.Offer;
import com.freberg.discorddeputy.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/epicGames")
@RequiredArgsConstructor
public class EpicGamesOfferController {

    private final OfferRepository repository;

    @GetMapping("/currentOffers")
    public Flux<Offer> getCurrentOffers() {
        return repository.findCurrentOffers(Instant.now());
    }

    @GetMapping("/upcomingOffers")
    public Flux<Offer> getUpcomingOffers() {
        return repository.findUpcomingOffers(Instant.now());
    }

    @GetMapping("/allOffers")
    public Flux<Offer> getAllOffers() {
        return repository.findAll();
    }
}
