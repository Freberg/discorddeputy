package com.freberg.discorddeputy.repository;

import com.freberg.discorddeputy.DiscordNotification;
import reactor.core.publisher.Mono;

interface CustomizedDiscordNotificationRepository {
    Mono<DiscordNotification> insertNotification(DiscordNotification notification);
}
