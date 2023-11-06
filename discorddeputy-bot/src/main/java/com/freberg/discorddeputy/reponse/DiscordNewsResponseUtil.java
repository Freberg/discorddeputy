package com.freberg.discorddeputy.reponse;

import java.time.Instant;

import com.freberg.discorddeputy.model.ImageUrl;
import com.freberg.discorddeputy.model.News;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public class DiscordNewsResponseUtil {

    private DiscordNewsResponseUtil() {
    }

    public static EmbedCreateSpec createNewsMessage(News news) {
        var builder = EmbedCreateSpec.builder()
                .title(news.getTitle())
                .url(news.getUrl())
                .timestamp(Instant.now())
                .color(Color.BLACK)
                .description(news.getContent());

        news.getImageUrls().stream()
                .findFirst()
                .map(ImageUrl::getUrl)
                .ifPresent(builder::image);

        return builder.build();
    }
}
