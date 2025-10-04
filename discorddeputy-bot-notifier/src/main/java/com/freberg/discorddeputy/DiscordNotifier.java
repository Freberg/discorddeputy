package com.freberg.discorddeputy;

import com.freberg.discorddeputy.api.DiscordNotification;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DiscordNotifier implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DiscordNotifier.class);
    private GatewayDiscordClient client;

    @Override
    public void run(ApplicationArguments args) {
        client = DiscordClient.create(System.getenv("DISCORD_TOKEN"))
                .login()
                .blockOptional()
                .orElseThrow();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(readyEvent -> log.info("Logged in as {}", readyEvent.getSelf().getUsername()));

        client.onDisconnect()
                .subscribe();
    }

    public void onNewNotification(DiscordNotification notification) {
        var embed = toEmbedCreateSpec(notification);
        var messageCreateRequest = MessageCreateRequest.builder()
                .embed(embed.asRequest())
                .build();
        dispatchMessage(messageCreateRequest);
    }


    private void dispatchMessage(MessageCreateRequest message) {
        client.getGuilds()
                .flatMap(guild -> guild.getChannels()
                        .ofType(TextChannel.class)
                        .filterWhen(channel -> channel.getEffectivePermissions(client.getSelfId())
                                .map(permissions -> permissions.contains(Permission.SEND_MESSAGES)
                                        && permissions.contains(Permission.VIEW_CHANNEL))
                        )
                        .map(TextChannel::getRestChannel)
                        .flatMap(channel -> channel.createMessage(message))
                )
                .subscribe();
    }

    private static EmbedCreateSpec toEmbedCreateSpec(DiscordNotification notification) {
        var builder = EmbedCreateSpec.builder()
                .title(notification.getTitle())
                .url(notification.getUrl())
                .timestamp(notification.getTimestamp())
                .color(Color.BLACK);

        if (notification.getDescriptionHeader() != null) {
            builder.addField(notification.getDescriptionHeader(), notification.getDescription(), false);
        } else {
            builder.description(notification.getDescription());
        }
        if (notification.getImageUrl() != null) {
            builder.image(notification.getImageUrl());
        }
        return builder.build();
    }
}