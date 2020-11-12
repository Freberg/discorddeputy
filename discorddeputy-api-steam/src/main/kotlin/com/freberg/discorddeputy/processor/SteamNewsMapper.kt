package com.freberg.discorddeputy.processor

import com.freberg.discorddeputy.constant.DiscordDeputyConstants
import com.freberg.discorddeputy.json.steam.SteamNews
import com.freberg.discorddeputy.message.ImageUrl
import com.freberg.discorddeputy.message.ImmutableImageUrl
import com.freberg.discorddeputy.message.ImmutableNews
import com.freberg.discorddeputy.message.News
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.collections.ArrayList

@Component
class SteamNewsMapper : MessageMapper<SteamNews, News> {

    private val STEAM_CLAN_IMAGE = "{STEAM_CLAN_IMAGE}";
    private val URL_CDN_STEAM = "https://cdn.cloudflare.steamstatic.com/steamcommunity/public/images/clans/"
    private val REGEX_STEAM_IMAGE_LINK = "\\{STEAM_CLAN_IMAGE}[^\\s-]*.[png|jpg|jpeg]"

    override fun mapMessage(inputMessage: SteamNews): News {
        return ImmutableNews.builder()
                .sourceTimestamp(inputMessage.date)
                .processTimestamp(Instant.now())
                .source(DiscordDeputyConstants.SOURCE_STEAM)
                .id(inputMessage.gid)
                .title(inputMessage.title)
                .content(extractDescription(inputMessage))
                .url(inputMessage.url)
                .imageUrls(extractImageUrls(inputMessage))
                .build()
    }

    private fun extractDescription(inputMessage: SteamNews): String {
        return inputMessage.contents.replace(REGEX_STEAM_IMAGE_LINK.toRegex(), "")
    }

    private fun extractImageUrls(inputMessage: SteamNews): Collection<ImageUrl> {
        val urls = ArrayList<ImageUrl>()
        urls.addAll(extractImageUrl(inputMessage))
        urls.addAll(extractSteamImageUrl(inputMessage))
        return urls;
    }

    fun extractImageUrl(news: SteamNews): Collection<ImageUrl> {
        return Optional.ofNullable(Jsoup.parse(news.contents))
                .map { document: org.jsoup.nodes.Document -> document.select("a") }
                .stream()
                .flatMap { it.stream() }
                .map { element: org.jsoup.nodes.Element -> element.attr("href") }
                .filter { obj: String? -> Objects.nonNull(obj) }
                .filter { link: String ->
                    link.endsWith(".png") || link.endsWith(".jpg")
                            || link.endsWith(".jpeg")
                }
                .map { toImageUrl(it, "EXTERNAL") }
                .collect(Collectors.toList())
    }

    private fun extractSteamImageUrl(news: SteamNews): Collection<ImageUrl> {
        return news.contents.split(" ").stream()
                .filter { it.startsWith(STEAM_CLAN_IMAGE) }
                .map { it.replace(STEAM_CLAN_IMAGE, URL_CDN_STEAM) }
                .map { toImageUrl(it, "INTERNAL") }
                .collect(Collectors.toList());
    }

    private fun toImageUrl(url: String, description: String): ImageUrl {
        return ImmutableImageUrl.builder()
                .url(url)
                .description(description)
                .build();
    }
}