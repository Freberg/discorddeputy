package com.freberg.discorddeputy.subcriber;

import com.freberg.discorddeputy.bot.DiscordDeputyBot;
import com.freberg.discorddeputy.message.EpicGamesOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@Slf4j
@RequiredArgsConstructor
@EnableBinding({Sink.class})
public class EpicGamesOfferSubscriber {

    private final DiscordDeputyBot discordDeputyBot;

    @StreamListener(Sink.INPUT)
    public void onNewEpicGamesOffers(EpicGamesOffer offer) {
        log.info("Received new offer with ID \"{}\" offers from queue", offer.getId());
        discordDeputyBot.onNewEpicGamesOffer(offer);
    }
}
