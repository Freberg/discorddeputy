package com.freberg.discorddeputy.subcriber;

import com.freberg.discorddeputy.bot.DiscordDeputyBot;
import com.freberg.discorddeputy.model.Offer;
import com.freberg.discorddeputy.model.News;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component
@Configuration
public class MessageSubscriber {

    private final DiscordDeputyBot discordDeputyBot;

    @Bean
    public Consumer<Offer> offerSink() {
        return offer -> {
            log.info("Received new offer with ID \"{}\" from queue", offer.getId());
            discordDeputyBot.onNewEpicGamesOffer(offer);
        };
    }

    @Bean
    public Consumer<News> newsSink() {
        return news -> {
            log.info("Received new news with ID \"{}\" from queue", news.getId());
            discordDeputyBot.onNewsSteamNews(news);
        };
    }
}
