package com.freberg.discorddeputy;

import com.freberg.discorddeputy.model.DiscordNotification;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
@SuppressWarnings("unused")
public class BotApplication {

    private final DiscordDeputyBot discordDeputyBot;

    public static EmbedCreateSpec toEmbedCreateSpec(DiscordNotification notification) {
        var builder = EmbedCreateSpec.builder()
                .title(notification.getTitle())
                .timestamp(notification.getTimestamp().toInstant())
                .color(Color.BLACK);

        if (notification.getDescriptionHeader() != null) {
            builder.addField(notification.getDescriptionHeader(), notification.getDescription(), false);
        } else {
            builder.description(notification.getDescription());
        }

        if (notification.getUrl() != null) {
            builder.url(notification.getUrl());
        }
        if (notification.getImageUrl() != null) {
            builder.image(notification.getImageUrl());
        }
        return builder.build();
    }

    @Bean
    public Consumer<DiscordNotification> sink() {
        return notification -> {
            log.info("Received notification with ID \"{}\" from \"{}\"", notification.getId(), notification.getTitle());
            discordDeputyBot.onNewNotification(notification);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class);
    }
}
