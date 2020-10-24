package com.freberg.discorddeputy.fetcher

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.freberg.discorddeputy.message.steam.SteamNews
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

private const val STEAM_GAMES_HOST = "http://api.steampowered.com"
private const val STEAM_GAMES_URI = "/ISteamNews/GetNewsForApp/v0002/?appid=594650,440&count=3&maxlength=300&format=json"

@Component
class StreamNewsFetcher(@Value("\${steam.pollFrequency.duration:30}")
                        private val pollFrequencyDuration: Long,
                        @Value("\${steam.pollFrequency.timeUnit:MINUTES}")
                        private val timeUnit: ChronoUnit) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val webClient = WebClient.create(STEAM_GAMES_HOST)
    private val objectMapper = jacksonObjectMapper()

    init {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
    }

    fun fetchNews(): Flux<SteamNews> = Flux.interval(Duration.ZERO, Duration.of(pollFrequencyDuration, timeUnit))
            .flatMap {
                webClient.get()
                        .uri(STEAM_GAMES_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
            }
            .flatMap { it.bodyToMono(String::class.java) }
            .map { deserialize(it) }
            .flatMap { Flux.fromIterable(it) }

    private fun deserialize(json: String) =
            try {
                Optional.of(objectMapper.readValue(json, SteamNewsResponse::class.java))
                        .map { it.appNews }
                        .map { it.newsItems }
                        .orElse(Collections.emptyList())
            } catch (e: JsonProcessingException) {
                log.error("Failed to deserialize message {}", json, e)
                Collections.emptyList()
            }

    data class SteamNewsResponse(var appNews: SteamAppNews)

    data class SteamAppNews(var newsItems: List<SteamNews>)
}