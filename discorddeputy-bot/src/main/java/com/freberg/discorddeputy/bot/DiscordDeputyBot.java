package com.freberg.discorddeputy.bot;

import javax.annotation.PostConstruct;
import java.util.Optional;

import com.freberg.discorddeputy.command.CommandFactory;
import com.freberg.discorddeputy.message.EpicGamesOffer;
import com.freberg.discorddeputy.reponse.DiscordResponseUtil;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.MessageCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordDeputyBot implements Runnable {

    private static final String ENV_VAR_DISCORD_TOKEN = "DISCORD_TOKEN";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final CommandFactory commandFactory;

    private GatewayDiscordClient client;

    @PostConstruct
    public void init() {
        new Thread(this, "Discord Bot")
                .start();
    }

    @Override
    public void run() {
        client = DiscordClientBuilder.create(getToken())
                                     .build()
                                     .login()
                                     .block();

        client.getEventDispatcher().on(ReadyEvent.class)
              .subscribe(this::onReadyEvent);

        client.getEventDispatcher().on(MessageCreateEvent.class)
              .subscribe(this::onMessageCreateEvent);

        client.onDisconnect()
              .subscribe();
    }

    public void onNewEpicGamesOffer(EpicGamesOffer offer) {
        client.getGuilds()
              .flatMap(Guild::getChannels)
              .filter(channel -> Channel.Type.GUILD_TEXT == channel.getType())
              .flatMap(channel -> channel.getRestChannel().createMessage(buildMessage(offer)))
              .subscribe();
    }

    private MessageCreateRequest buildMessage(EpicGamesOffer offer) {
        EmbedCreateSpec embedCreateSpec = new EmbedCreateSpec();
        DiscordResponseUtil.createEpicGamesOfferMessage(offer, embedCreateSpec, false);
        return MessageCreateRequest.builder()
                                   .embed(embedCreateSpec.asRequest())
                                   .build();
    }

    private void onReadyEvent(ReadyEvent readyEvent) {
        logger.info("Logged in as {}", readyEvent.getSelf().getUsername());
    }

    private void onMessageCreateEvent(MessageCreateEvent messageCreateEvent) {
        logger.info("Received message from \"{}\"", messageCreateEvent.getMember().orElse(null));
        Optional.of(messageCreateEvent)
                .map(MessageCreateEvent::getMessage)
                .map(Message::getContent)
                .map(this::getCommandFromMessage)
                .map(commandFactory::resolveCommandFromString)
                .ifPresent(command -> command.accept(messageCreateEvent.getMessage()));
    }

    private String getCommandFromMessage(String message) {
        return message;
    }

    private String getToken() {
        return Optional.ofNullable(System.getenv(ENV_VAR_DISCORD_TOKEN))
                       .filter(Strings::isNotBlank)
                       .orElseThrow(() -> new RuntimeException("No discord token found, environment variable \"" +
                                                               ENV_VAR_DISCORD_TOKEN + "\" should be set"));
    }
}
