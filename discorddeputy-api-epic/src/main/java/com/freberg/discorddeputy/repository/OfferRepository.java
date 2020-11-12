package com.freberg.discorddeputy.repository;

import java.time.Instant;

import com.freberg.discorddeputy.message.Offer;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface OfferRepository extends ReactiveMongoRepository<Offer, String> {

    @Query(value = "{ 'startTime' : { $lte :  ?0 }, 'endTime' : { $gte :  ?0 } }")
    Flux<Offer> findCurrentOffers(Instant currentTime);

    @Query(value = "{ 'startTime' : { $gt :  ?0 } }")
    Flux<Offer> findUpcomingOffers(Instant currentTime);
}
