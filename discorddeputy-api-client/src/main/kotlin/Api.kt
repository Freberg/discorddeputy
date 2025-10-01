package com.freberg.discorddeputy.api

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.jackson.*
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeParseException

class ApiClient(val baseUrl: String, engine: HttpClientEngine) {

    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            jackson {
                registerModule(InstantModule())
            }
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

class InstantSerializer : JsonSerializer<Instant>() {
    override fun serialize(value: Instant?, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toString())
    }
}

class InstantDeserializer : StdDeserializer<Instant>(Instant::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext): Instant? {
        val dateString = p!!.text
        return try {
            Instant.parse(dateString)
        } catch (e: DateTimeParseException) {
            throw IOException("Failed to parse Instant from JSON: $dateString", e)
        }
    }
}

class InstantModule : SimpleModule() {
    init {
        addSerializer(Instant::class.java, InstantSerializer())
        addDeserializer(Instant::class.java, InstantDeserializer())
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