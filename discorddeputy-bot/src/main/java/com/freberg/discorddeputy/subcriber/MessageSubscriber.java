package com.freberg.discorddeputy.subcriber;

import java.util.Optional;

import com.freberg.discorddeputy.bot.DiscordDeputyBot;
import com.freberg.discorddeputy.constant.DiscordDeputyConstants;
import com.freberg.discorddeputy.message.MessageDeserializer;
import com.freberg.discorddeputy.message.MessageType;
import com.freberg.discorddeputy.model.News;
import com.freberg.discorddeputy.model.Offer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

@Slf4j
@RequiredArgsConstructor
@EnableBinding({Sink.class})
public class MessageSubscriber {

    private final MessageDeserializer messageDeserializer = new MessageDeserializer();
    private final DiscordDeputyBot discordDeputyBot;

    @StreamListener(Sink.INPUT)
    public void onNewMessage(Message<Object> message) {
        Optional.of(message.getHeaders())
                .map(headers -> headers.get(DiscordDeputyConstants.MESSAGE_HEADER_MESSAGE_TYPE))
                .map(Object::toString)
                .map(MessageType::valueOf)
                .map(messageType -> messageDeserializer.deserialize((String) message.getPayload(), messageType))
                .ifPresentOrElse(payload -> {
                    if (payload instanceof Offer) {
                        onNewEpicGamesOffer((Offer) payload);
                    } else if (payload instanceof News) {
                        onNewSteamNews((News) payload);
                    } else {
                        log.error("Unsupported message type \"{}\"", payload.getClass());
                    }
                }, () -> log.error("Failed to deserialize message \"{}\"", message));
    }

    private void onNewEpicGamesOffer(Offer offer) {
        log.info("Received new offer with ID \"{}\" from queue", offer.getId());
        discordDeputyBot.onNewEpicGamesOffer(offer);
    }

    private void onNewSteamNews(News news) {
        log.info("Received new news with ID \"{}\" from queue", news.getId());
        discordDeputyBot.onNewsSteamNews(news);
    }
}
