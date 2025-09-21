package com.freberg.discorddeputy;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface DiscordNotificationRepository extends R2dbcRepository<DiscordNotification, String> {

    String SELECT_DISCORD_NOTIFICATION = """
            SELECT id, source, \
            data->>'type' as type, \
            (data->>'timestamp')::timestamptz as timestamp, \
            data->>'title' as title, \
            COALESCE(data->>'descriptionHeader', '')::TEXT as descriptionHeader, \
            data->>'description' as description, \
            COALESCE(data->>'url', '')::TEXT as url, \
            COALESCE(data->>'imageUrl', '')::TEXT as imageUrl, \
            COALESCE(data->>'thumbnailUrl', '')::TEXT as thumbnailUrl \
            FROM discord_notifications\s""";

    @Query(SELECT_DISCORD_NOTIFICATION +
            "WHERE data->>'type' = 'CURRENT_OFFER' AND data->>'startTime' <= :currentTime AND data->>'endTime' >= :currentTime")
    Flux<DiscordNotification> findCurrentOffers(String currentTime);

    @Query(SELECT_DISCORD_NOTIFICATION +
            "WHERE data->>'type' = 'UPCOMING_OFFER' AND data->>'startTime' > :currentTime")
    Flux<DiscordNotification> findUpcomingOffers(String currentTime);

    @Query(SELECT_DISCORD_NOTIFICATION +
            "WHERE data->>'type' = 'NEWS' AND (data->>'timestamp')::timestamptz > :fromTime::timestamptz")
    Flux<DiscordNotification> findLatestNews(String fromTime);
}