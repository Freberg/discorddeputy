package com.freberg.discorddeputy.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.jackson3.*
import java.time.Instant

class ApiClient(val baseUrl: String, engine: HttpClientEngine) {

    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            jackson()
        }
    }

    constructor(baseUrl: String) : this(baseUrl, CIO.create())

    suspend fun upcomingOffers(): List<DiscordNotification> {
        return client.get("$baseUrl/upcomingOffers").body()
    }

    suspend fun currentOffers(): List<DiscordNotification> {
        return client.get("$baseUrl/currentOffers").body()
    }

    suspend fun latestNews(): List<DiscordNotification> {
        return client.get("$baseUrl/latestNews").body()
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordNotification(
    val id: String,
    val timestamp: Instant,
    val title: String,
    val description: String,
    val url: String,
    val source: String,
    val type: String,
    val descriptionHeader: String?,
    val imageUrl: String?
)