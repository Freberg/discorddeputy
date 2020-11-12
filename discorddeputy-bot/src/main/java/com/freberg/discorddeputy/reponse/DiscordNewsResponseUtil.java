package com.freberg.discorddeputy.reponse;

import java.time.Instant;

import com.freberg.discorddeputy.model.ImageUrl;
import com.freberg.discorddeputy.model.News;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;

public class DiscordNewsResponseUtil {

    private DiscordNewsResponseUtil() {
    }

    public static void createNewsMessage(News news, EmbedCreateSpec spec) {
        spec.setTitle(news.getTitle());
        spec.setUrl(news.getUrl());
        spec.setTimestamp(Instant.now());
        spec.setColor(Color.BLACK);
        spec.setDescription(news.getContent());
        news.getImageUrls().stream()
            .findFirst()
            .map(ImageUrl::getUrl)
            .ifPresent(spec::setImage);
    }
}
