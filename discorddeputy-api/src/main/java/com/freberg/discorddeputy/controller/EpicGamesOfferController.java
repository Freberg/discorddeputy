package com.freberg.discorddeputy.controller;

import com.freberg.discorddeputy.message.EpicGamesOffer;
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
        return repository.findAll();
    }

    @GetMapping("/upcomingOffers")
    public Flux<EpicGamesOffer> getUpcomingOffers() {
        return repository.findAll();
    }

    @GetMapping("/allOffers")
    public Flux<EpicGamesOffer> getAllOffers() {
        return repository.findAll();
    }
}
