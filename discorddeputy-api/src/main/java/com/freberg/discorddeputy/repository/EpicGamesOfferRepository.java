package com.freberg.discorddeputy.repository;

import com.freberg.discorddeputy.message.EpicGamesOffer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EpicGamesOfferRepository extends ReactiveMongoRepository<EpicGamesOffer, String> {
}
