package com.freberg.discorddeputy.api

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class ApiClientTest {

    @Test
    fun `upcomingOffers returns a list of DiscordNotification`() = runBlocking {
        val jsonResponse = """
            [
                {
                    "id": "1",
                    "timestamp": "2025-01-01T10:00:00Z",
                    "title": "Upcoming Offer 1",
                    "description": "Description 1",
                    "url": "https://example.com/1",
                    "source": "SourceA",
                    "type": "Offer",
                    "descriptionHeader": null,
                    "imageUrl": null
                }
            ]
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/upcomingOffers" -> {
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }

                else -> respond("Not Found", HttpStatusCode.NotFound)
            }
        }

        val apiClient = ApiClient(baseUrl = "http://localhost", engine = mockEngine)

        val offers = apiClient.upcomingOffers()

        assertEquals(1, offers.size)
        val offer = offers.first()
        assertEquals("1", offer.id)
        assertEquals("Upcoming Offer 1", offer.title)
        assertEquals(Instant.parse("2025-01-01T10:00:00Z"), offer.timestamp)
    }
}