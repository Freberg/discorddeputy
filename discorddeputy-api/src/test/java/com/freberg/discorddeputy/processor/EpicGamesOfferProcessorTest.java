package com.freberg.discorddeputy.processor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.freberg.discorddeputy.message.epic.EpicGamesOffer;
import com.freberg.discorddeputy.repository.EpicGamesOfferRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import reactor.core.publisher.Mono;

class EpicGamesOfferProcessorTest {

    private EpicGamesOfferProcessor processor;
    private EpicGamesOfferRepository repository;
    private MessageChannel channel;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(EpicGamesOfferRepository.class);
        channel = Mockito.mock(MessageChannel.class);

        Source source = Mockito.mock(Source.class);
        Mockito.when(source.output())
               .thenReturn(channel);

        processor = new EpicGamesOfferProcessor(repository, source);

        Set<String> seemIds = new HashSet<>();
        Mockito.when(repository.existsById(Mockito.anyString()))
               .thenAnswer(invocation -> {
                   String id = (String) invocation.getArguments()[0];
                   return Mono.just(!seemIds.add(id));
               });
    }

    @Test
    void onlyPersistOffersWithNewId() throws InterruptedException {
        AtomicInteger saveCallCount = new AtomicInteger();
        Mockito.when(repository.save(Mockito.any()))
               .thenAnswer(invocation -> {
                   EpicGamesOffer offer = (EpicGamesOffer) invocation.getArguments()[0];
                   saveCallCount.incrementAndGet();
                   return Mono.just(offer);
               });

        processor.onNewEpicGamesOffers(newEpicGamesOffer("1"));
        processor.onNewEpicGamesOffers(newEpicGamesOffer("1"));
        processor.onNewEpicGamesOffers(newEpicGamesOffer("2"));
        processor.onNewEpicGamesOffers(newEpicGamesOffer("1"));

        // Wait for asynchronous computations to complete
        Thread.sleep(200);
        Assertions.assertEquals(2, saveCallCount.intValue());
    }

    @Test
    void onlyDispatchOffersWithNewId() throws InterruptedException {
        AtomicInteger saveCallCount = new AtomicInteger();

        Mockito.when(repository.save(Mockito.any()))
               .thenAnswer(invocation -> {
                   EpicGamesOffer offer = (EpicGamesOffer) invocation.getArguments()[0];
                   return Mono.just(offer);
               });

        Mockito.when(channel.send(Mockito.any()))
               .thenAnswer(invocation -> {
                   EpicGamesOffer offer = ((Message<EpicGamesOffer>) invocation.getArguments()[0])
                           .getPayload();
                   saveCallCount.incrementAndGet();
                   return true;
               });

        processor.onNewEpicGamesOffers(newEpicGamesOffer("1"));
        processor.onNewEpicGamesOffers(newEpicGamesOffer("1"));
        processor.onNewEpicGamesOffers(newEpicGamesOffer("2"));
        processor.onNewEpicGamesOffers(newEpicGamesOffer("1"));

        // Wait for asynchronous computations to complete
        Thread.sleep(200);
        Assertions.assertEquals(2, saveCallCount.intValue());
    }

    private EpicGamesOffer newEpicGamesOffer(String id) {
        EpicGamesOffer offer = Mockito.mock(EpicGamesOffer.class);
        Mockito.when(offer.getId())
               .thenReturn(id);
        return offer;
    }
}