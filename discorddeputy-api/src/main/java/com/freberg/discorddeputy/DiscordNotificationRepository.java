package com.freberg.discorddeputy;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface DiscordNotificationRepository extends ReactiveMongoRepository<DiscordNotification, String> {

    @Query(value = "{ 'type' : { $eq : 'CURRENT_OFFER' }, 'startTime' : { $lte :  ?0 }, 'endTime' : { $gte :  ?0 } }")
    Flux<DiscordNotification> findCurrentOffers(String currentTime);

    @Query(value = "{ 'type' : { $eq : 'UPCOMING_OFFER' }, 'startTime' : { $gt :  ?0 } }")
    Flux<DiscordNotification> findUpcomingOffers(String currentTime);

    @Query(value = "{ 'type' : { $eq : 'NEWS' }, 'startTime' : { $gt :  ?0 } }")
    Flux<DiscordNotification> findLatestNews(String fromTime);
}
