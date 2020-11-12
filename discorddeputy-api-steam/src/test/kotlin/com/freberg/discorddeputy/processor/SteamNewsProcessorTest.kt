package com.freberg.discorddeputy.processor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.freberg.discorddeputy.json.steam.SteamNews
import com.freberg.discorddeputy.message.News
import com.freberg.discorddeputy.repository.NewsRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.MessageChannel
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicInteger

class EpicGamesOfferProcessorTest {

    private val ID_PLACE_HOLDER = "<ID>"
    private val TEST_NEWS = "{\"gid\":\"" + ID_PLACE_HOLDER + "\",\"title\":\"Update 1.4.7 Patch notes\",\"url\":\"https://steamstore-a.akamaihd.net/news/externalpost/steam_community_announcements/3872494575583474271\",\"is_external_url\":true,\"author\":\"Cry_Ic3man2k\",\"contents\":\"{STEAM_CLAN_IMAGE}/30897473/0196523e4be17755db3b3256d4cff9b168a5433e.jpg {STEAM_CLAN_IMAGE}/30897473/ebebc587eb1ef52c9419f7aad63f194322308462.png Fixed an issue that caused the menu to become slow/laggy or result in being disconnected from the game. Implemented a tentative fix for a crash that could...\",\"feedlabel\":\"Community Announcements\",\"date\":1605087837,\"feedname\":\"steam_community_announcements\",\"feed_type\":1,\"appid\":594650,\"tags\":[\"patchnotes\",\"mod_reviewed\",\"mod_require_rereview\"]}";

    private lateinit var processor: SteamNewsProcessor
    private lateinit var repository: NewsRepository
    private lateinit var channel: MessageChannel
    private lateinit var objectMapper: ObjectMapper;

    @BeforeEach
    fun setUp() {
        repository = Mockito.mock(NewsRepository::class.java)
        channel = Mockito.mock(MessageChannel::class.java)
        objectMapper = ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)

        val source = Mockito.mock(Source::class.java)
        Mockito.`when`(source.output())
                .thenReturn(channel)
        processor = SteamNewsProcessor(SteamNewsMapper(), repository, source)
        val seemIds: MutableSet<String> = HashSet()
        Mockito.`when`(repository.existsById(Mockito.anyString()))
                .thenAnswer(Answer<Any> { invocation: InvocationOnMock ->
                    val id = invocation.arguments[0] as String
                    Mono.just(!seemIds.add(id))
                })
    }

    @Test
    @Throws(InterruptedException::class)
    fun onlyPersistNewsWithNewId() {
        val saveCallCount = AtomicInteger()
        Mockito.`when`(repository.save(Mockito.any()))
                .thenAnswer(Answer<Any> { invocation: InvocationOnMock ->
                    val news = invocation.arguments[0] as News
                    saveCallCount.incrementAndGet()
                    Mono.just(news)
                })
        processor.onSteamNews(newSteamNews("1"))
        processor.onSteamNews(newSteamNews("1"))
        processor.onSteamNews(newSteamNews("2"))
        processor.onSteamNews(newSteamNews("1"))

        // Wait for asynchronous computations to complete
        Thread.sleep(200)
        Assertions.assertEquals(2, saveCallCount.toInt())
    }

    @Test
    @Throws(InterruptedException::class)
    fun onlyDispatchNewsWithNewId() {
        val saveCallCount = AtomicInteger()
        Mockito.`when`(repository.save(Mockito.any()))
                .thenAnswer(Answer<Any> { invocation: InvocationOnMock ->
                    val news = invocation.arguments[0] as News
                    Mono.just(news)
                })
        Mockito.`when`(channel.send(Mockito.any()))
                .thenAnswer {
                    saveCallCount.incrementAndGet()
                    true
                }
        processor.onSteamNews(newSteamNews("1"))
        processor.onSteamNews(newSteamNews("1"))
        processor.onSteamNews(newSteamNews("2"))
        processor.onSteamNews(newSteamNews("1"))

        // Wait for asynchronous computations to complete
        Thread.sleep(200)
        Assertions.assertEquals(2, saveCallCount.toInt())
    }

    private fun newSteamNews(gid: String): SteamNews {
        val json: String = TEST_NEWS.replace(ID_PLACE_HOLDER, gid)
        return objectMapper.readValue(json, SteamNews::class.java)
    }
}