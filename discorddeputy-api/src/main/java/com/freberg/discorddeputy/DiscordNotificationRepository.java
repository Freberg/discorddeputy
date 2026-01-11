package com.freberg.discorddeputy;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import java.util.List;

public interface DiscordNotificationRepository extends Repository<DiscordNotification, String> {

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
    List<DiscordNotification> findCurrentOffers(String currentTime);

    @Query(SELECT_DISCORD_NOTIFICATION +
            "WHERE data->>'type' = 'UPCOMING_OFFER' AND data->>'startTime' > :currentTime")
    List<DiscordNotification> findUpcomingOffers(String currentTime);

    @Query(SELECT_DISCORD_NOTIFICATION +
            "WHERE data->>'type' = 'NEWS' AND (data->>'timestamp')::timestamptz > :fromTime::timestamptz")
    List<DiscordNotification> findLatestNews(String fromTime);
}
