package com.freberg.discorddeputy.processor;

import com.freberg.discorddeputy.constant.DiscordDeputyConstants;
import com.freberg.discorddeputy.message.MessageType;
import com.freberg.discorddeputy.json.epic.EpicGamesOffer;
import com.freberg.discorddeputy.message.Offer;
import com.freberg.discorddeputy.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@EnableBinding({Sink.class, Source.class})
@RequiredArgsConstructor
public class EpicGamesOfferProcessor {

    private final EpicGamesOfferMapper offerMapper;
    private final OfferRepository repository;
    private final Source source;

    @StreamListener(Sink.INPUT)
    public void onEpicGamesOffer(EpicGamesOffer input) {
        Offer offer = offerMapper.mapMessage(input);
        repository.existsById(offer.getId())
                  .flatMap(idExists -> {
                      if (Boolean.TRUE.equals(idExists)) {
                          return Mono.empty();
                      } else {
                          return persist(offer)
                                  .then(dispatch(offer));
                      }
                  })
                  .subscribe();
    }

    private Mono<Offer> persist(Offer offer) {
        log.info("Persisted new offer with ID \"{}\" to DB", offer.getId());
        return repository.save(offer);
    }

    private Mono<Offer> dispatch(Offer offer) {
        return Mono.fromCallable(() -> {
            source.output().send(MessageBuilder.withPayload(offer)
                                               .setHeader(DiscordDeputyConstants.MESSAGE_HEADER_MESSAGE_TYPE,
                                                       MessageType.OFFER)
                                               .build());
            log.info("Put new offer with ID \"{}\" to queue", offer.getId());
            return offer;
        }).subscribeOn(Schedulers.elastic());
    }
}
