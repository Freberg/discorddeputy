package com.freberg.discorddeputy.bot;

import com.freberg.discorddeputy.command.CommandFactory;
import com.freberg.discorddeputy.model.News;
import com.freberg.discorddeputy.model.Offer;
import com.freberg.discorddeputy.reponse.DiscordNewsResponseUtil;
import com.freberg.discorddeputy.reponse.DiscordOfferResponseUtil;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.MessageCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordDeputyBot implements ApplicationRunner {

    private static final String ENV_VAR_DISCORD_TOKEN = "DISCORD_TOKEN";
    private final CommandFactory commandFactory;

    private GatewayDiscordClient client;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        client = DiscordClient.create(getToken()).login().block();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(this::onReadyEvent);

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(this::onMessageCreateEvent);

        client.onDisconnect()
                .subscribe();
    }

    public void onNewEpicGamesOffer(Offer offer) {
        dispatchMessage(buildOfferMessage(offer));
    }

    public void onNewsSteamNews(News news) {
        dispatchMessage(buildNewsMessage(news));
    }

    private void dispatchMessage(MessageCreateRequest message) {
        client.getGuilds()
                .flatMap(Guild::getChannels)
                .filter(channel -> Channel.Type.GUILD_TEXT == channel.getType())
                .map(Channel::getRestChannel)
                .flatMap(channel -> channel.createMessage(message))
                .subscribe();
    }

    private MessageCreateRequest buildOfferMessage(Offer offer) {
        var embed = DiscordOfferResponseUtil.createOfferMessage(offer, false);
        return MessageCreateRequest.builder()
                .embed(embed.asRequest())
                .build();
    }

    private MessageCreateRequest buildNewsMessage(News news) {
        var embed = DiscordNewsResponseUtil.createNewsMessage(news);
        return MessageCreateRequest.builder()
                .embed(embed.asRequest())
                .build();
    }

    private void onReadyEvent(ReadyEvent readyEvent) {
        log.info("Logged in as {}", readyEvent.getSelf().getUsername());
    }

    private void onMessageCreateEvent(MessageCreateEvent messageCreateEvent) {
        boolean isBot = messageCreateEvent.getMessage().getUserData().bot()
                .toOptional()
                .orElse(false);
        if (isBot) {
            return;
        }

        log.info("Received message from \"{}\"", messageCreateEvent.getMember().orElse(null));
        Optional.of(messageCreateEvent)
                .map(MessageCreateEvent::getMessage)
                .map(Message::getContent)
                .map(this::getCommandFromMessage)
                .map(commandFactory::resolveCommandFromString)
                .ifPresent(command -> command.accept(messageCreateEvent.getMessage()));
    }

    private String getCommandFromMessage(String message) {
        return Optional.ofNullable(message)
                .map(str -> str.split(" "))
                .filter(words -> words.length > 0)
                .map(words -> words[0])
                .orElse(null);
    }

    private String getToken() {
        return Optional.ofNullable(System.getenv(ENV_VAR_DISCORD_TOKEN))
                .filter(Strings::isNotBlank)
                .orElseThrow(() -> new RuntimeException("No discord token found, environment variable \"" +
                        ENV_VAR_DISCORD_TOKEN + "\" should be set"));
    }
}