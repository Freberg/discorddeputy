package com.freberg.discorddeputy.repository;

import com.freberg.discorddeputy.DiscordNotification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordNotificationRepository extends ReactiveCrudRepository<DiscordNotification, String>,
        CustomizedDiscordNotificationRepository {
}
