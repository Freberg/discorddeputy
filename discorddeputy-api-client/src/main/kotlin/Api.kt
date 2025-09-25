package com.freberg.discorddeputy.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeParseException

class ApiClient(val baseUrl: String, engine: HttpClientEngine) {

    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            gson {
                registerTypeAdapter(Instant::class.java, InstantAdapter())
            }
        }
    }

    constructor(baseUrl: String) : this(baseUrl, Apache.create())

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

class InstantAdapter : JsonSerializer<Instant>, JsonDeserializer<Instant> {
    override fun serialize(src: Instant, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Instant {
        return try {
            Instant.parse(json.asString)
        } catch (e: DateTimeParseException) {
            throw JsonParseException("Failed to parse Instant from JSON: ${json.asString}", e)
        }
    }
}

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