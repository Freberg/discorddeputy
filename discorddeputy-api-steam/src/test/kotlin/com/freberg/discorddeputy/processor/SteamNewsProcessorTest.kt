package com.freberg.discorddeputy.processor

import com.freberg.discorddeputy.message.epic.EpicGamesOffer
import com.freberg.discorddeputy.message.steam.SteamNews
import com.freberg.discorddeputy.repository.StreamNewsRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicInteger

class EpicGamesOfferProcessorTest {

    private lateinit var processor: SteamNewsProcessor
    private lateinit var repository: StreamNewsRepository
    private lateinit var channel: MessageChannel

    @BeforeEach
    fun setUp() {
        repository = Mockito.mock(StreamNewsRepository::class.java)
        channel = Mockito.mock(MessageChannel::class.java)
        val source = Mockito.mock(Source::class.java)
        Mockito.`when`(source.output())
                .thenReturn(channel)
        processor = SteamNewsProcessor(repository, source)
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
                    val offer = invocation.arguments[0] as EpicGamesOffer
                    saveCallCount.incrementAndGet()
                    Mono.just(offer)
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
                    val offer = invocation.arguments[0] as EpicGamesOffer
                    Mono.just(offer)
                })
        Mockito.`when`(channel.send(Mockito.any()))
                .thenAnswer { invocation: InvocationOnMock ->
                    val offer = (invocation.arguments[0] as Message<*>)
                            .payload
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
        val offer = Mockito.mock(SteamNews::class.java)
        Mockito.`when`(offer.id)
                .thenReturn(gid)
        return offer
    }
}