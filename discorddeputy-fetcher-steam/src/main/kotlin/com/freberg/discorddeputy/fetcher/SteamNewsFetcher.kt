package com.freberg.discorddeputy.fetcher

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.freberg.discorddeputy.json.steam.SteamNews
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

private const val STEAM_GAMES_HOST = "http://api.steampowered.com"
private const val STEAM_GAMES_URI =
        "/ISteamNews/GetNewsForApp/v0002/?appid=APP_ID,440&count=3&maxlength=300&format=json"

@Component
class SteamNewsFetcher(
        @Value("\${steam.pollFrequency.duration:30}")
        private val pollFrequencyDuration: Long,
        @Value("\${steam.pollFrequency.timeUnit:MINUTES}")
        private val timeUnit: ChronoUnit,
        @Value("\${steam.apps:594650}")
        private val appIds: List<String>
) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val webClient = WebClient.create(STEAM_GAMES_HOST)
    private val objectMapper = jacksonObjectMapper()

    init {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
    }

    fun fetchNews(): Flux<SteamNews> =
            Flux.interval(Duration.ZERO, Duration.of(pollFrequencyDuration, timeUnit))
                    .flatMap {
                        Flux.fromIterable(appIds)
                                .flatMap { retrieveNews(it) }
                    }

    fun retrieveNews(appId: String): Flux<SteamNews> =
            try {
                webClient.get()
                        .uri(getUri(appId))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono<String>()
                        .onErrorResume {
                            log.error("Failed to fetch data from epic games", it)
                            Mono.empty()
                        }
                        .map { deserialize(it) }
                        .flatMapMany { Flux.fromIterable(it) }
            } catch (e: Exception) {
                log.error("Failed to retrieve steam news", e)
                Flux.empty()
            }

    private fun getUri(appId: String): String = STEAM_GAMES_URI.replace("APP_ID", appId)

    private fun deserialize(json: String) =
            try {
                Optional.of(objectMapper.readValue(json, SteamNewsResponse::class.java))
                        .map { it.appNews }
                        .map { it.newsItems }
                        .orElse(Collections.emptyList())
            } catch (e: Exception) {
                log.error("Failed to deserialize message {}", json, e)
                Collections.emptyList()
            }

    data class SteamNewsResponse(var appNews: SteamAppNews)

    data class SteamAppNews(var newsItems: List<SteamNews>)
}