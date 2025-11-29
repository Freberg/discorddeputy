package com.freberg.discorddeputy.repository;

import com.freberg.discorddeputy.DiscordNotification;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Repository
class CustomizedDiscordNotificationRepositoryImpl implements CustomizedDiscordNotificationRepository {

    private final DatabaseClient databaseClient;
    private final ObjectMapper objectMapper;

    CustomizedDiscordNotificationRepositoryImpl(DatabaseClient databaseClient, ObjectMapper objectMapper) {
        this.databaseClient = databaseClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<DiscordNotification> insertNotification(DiscordNotification notification) {
        return Mono.fromCallable(() -> {
                    try {
                        return objectMapper.writeValueAsString(notification.fields());
                    } catch (JacksonException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(data -> {
                    String sql = "INSERT INTO discord_notifications (id, source, data) VALUES (:id, :source, :data::jsonb) ON CONFLICT (id) DO NOTHING";
                    return databaseClient.sql(sql)
                            .bind("id", notification.id())
                            .bind("source", notification.source())
                            .bind("data", data)
                            .fetch()
                            .rowsUpdated()
                            .flatMap(rowsAffected -> {
                                if (rowsAffected > 0) {
                                    return Mono.just(notification);
                                } else {
                                    return Mono.empty();
                                }
                            });
                });
    }
}