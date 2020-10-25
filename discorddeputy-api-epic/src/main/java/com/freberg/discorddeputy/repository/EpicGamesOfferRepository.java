package com.freberg.discorddeputy.repository;

import com.freberg.discorddeputy.message.epic.EpicGamesOffer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EpicGamesOfferRepository extends ReactiveMongoRepository<EpicGamesOffer, String> {
}
