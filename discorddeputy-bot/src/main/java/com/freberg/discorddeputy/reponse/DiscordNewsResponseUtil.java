package com.freberg.discorddeputy.reponse;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.freberg.discorddeputy.message.steam.SteamNews;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.jsoup.Jsoup;

public class DiscordNewsResponseUtil {

    private static final String URL_CDN_STEAM = "https://cdn.cloudflare.steamstatic.com/steamcommunity/public/images/clans/";
    private static final String REGEX_STEAM_IMAGE_LINK = "\\{STEAM_CLAN_IMAGE}.*.png";

    private DiscordNewsResponseUtil() {
    }

    public static void createSteamNewsMessage(SteamNews news, EmbedCreateSpec spec) {
        spec.setTitle(news.getTitle());
        spec.setUrl(news.getUrl());
        spec.setTimestamp(Instant.now());
        spec.setColor(Color.BLACK);

        spec.setDescription(extractDescription(news));

        Optional.ofNullable(extractImageUrl(news))
                .ifPresent(spec::setImage);
    }

    private static String extractDescription(SteamNews news) {
        String text = Jsoup.parse(news.getContents()).text();
        text = text.replaceAll(REGEX_STEAM_IMAGE_LINK, "");
        return text.trim();
    }

    private static String extractImageUrl(SteamNews news) {
        return Optional.ofNullable(Jsoup.parse(news.getContents()))
                       .map(document -> document.select("a"))
                       .stream()
                       .flatMap(Collection::stream)
                       .map(element -> element.attr("href"))
                       .filter(Objects::nonNull)
                       .filter(link -> link.endsWith(".jpg") || link.endsWith(".png"))
                       .findFirst()
                       .orElseGet(() -> extractSteamImageUrl(news));
    }

    private static String extractSteamImageUrl(SteamNews news) {
        Matcher matcher = Pattern.compile(REGEX_STEAM_IMAGE_LINK).matcher(news.getContents());
        return Optional.of(matcher.find())
                .filter(b -> b)
                .map(exists -> matcher.group(0))
                .map(match -> match.split(" "))
                .map(matches -> matches[0])
                .map(match -> match.replace("{STEAM_CLAN_IMAGE}", URL_CDN_STEAM))
                .orElse(null);
    }
}
