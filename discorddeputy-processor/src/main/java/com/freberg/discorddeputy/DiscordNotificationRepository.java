package com.freberg.discorddeputy;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DiscordNotificationRepository extends ReactiveMongoRepository<DiscordNotification, String> {

}
