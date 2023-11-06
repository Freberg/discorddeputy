package com.freberg.discorddeputy.processor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freberg.discorddeputy.json.epic.EpicGamesOffer;
import com.freberg.discorddeputy.message.Offer;
import com.freberg.discorddeputy.repository.OfferRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

class EpicGamesOfferProcessorTest {

    private static final String ID_PLACE_HOLDER = "<ID>";
    private static final String TEST_OFFER = "{\"title\":\"Dungeons 3\",\"id\":\"" + ID_PLACE_HOLDER + "\",\"namespace\":\"213259527d2f41baa90cbe0ae5555271\",\"description\":\"Dungeons 3\",\"effectiveDate\":\"2020-11-05T16:00:00.000Z\",\"keyImages\":[{\"type\":\"OfferImageWide\",\"url\":\"https://cdn1.epicgames.com/213259527d2f41baa90cbe0ae5555271/offer/EGS_Dungeons3_RealmforgeStudios_S3-2560x1440-21b5fe0f44bc1f4c23a68bf6e159be40.jpg\"},{\"type\":\"OfferImageTall\",\"url\":\"https://cdn1.epicgames.com/213259527d2f41baa90cbe0ae5555271/offer/EGS_Dungeons3_RealmforgeStudios_S4-1200x1600-1bd28634e22cae5706b41f0e3ef9cc0c.jpg\"},{\"type\":\"DieselStoreFrontWide\",\"url\":\"https://cdn1.epicgames.com/213259527d2f41baa90cbe0ae5555271/offer/EGS_Dungeons3_RealmforgeStudios_S3-2560x1440-21b5fe0f44bc1f4c23a68bf6e159be40.jpg\"},{\"type\":\"DieselStoreFrontTall\",\"url\":\"https://cdn1.epicgames.com/213259527d2f41baa90cbe0ae5555271/offer/EGS_Dungeons3_RealmforgeStudios_S4-1200x1600-1bd28634e22cae5706b41f0e3ef9cc0c.jpg\"},{\"type\":\"Thumbnail\",\"url\":\"https://cdn1.epicgames.com/213259527d2f41baa90cbe0ae5555271/offer/EGS_Dungeons3_RealmforgeStudios_S4-1200x1600-1bd28634e22cae5706b41f0e3ef9cc0c.jpg\"},{\"type\":\"CodeRedemption_340x440\",\"url\":\"https://cdn1.epicgames.com/213259527d2f41baa90cbe0ae5555271/offer/EGS_Dungeons3_RealmforgeStudios_S4-1200x1600-1bd28634e22cae5706b41f0e3ef9cc0c.jpg\"}],\"seller\":{\"id\":\"o-37ss7wypkskvyj58j9s7hhltxtujac\",\"name\":\"Kalypso Media Group GmbH\"},\"productSlug\":\"dungeons-3/home\",\"urlSlug\":\"ragdollgeneralaudience\",\"url\":null,\"items\":[{\"id\":\"b582b50fe82045c8bce722311a593081\",\"namespace\":\"213259527d2f41baa90cbe0ae5555271\"}],\"customAttributes\":[{\"key\":\"com.epicgames.app.blacklist\",\"value\":\"[]\"},{\"key\":\"publisherName\",\"value\":\"Kalypso Media\"},{\"key\":\"developerName\",\"value\":\"Realmforge Studios\"},{\"key\":\"com.epicgames.app.productSlug\",\"value\":\"dungeons-3/home\"}],\"categories\":[{\"path\":\"freegames\"},{\"path\":\"games\"},{\"path\":\"games/edition/base\"},{\"path\":\"games/edition\"},{\"path\":\"applications\"}],\"tags\":[{\"id\":\"1203\"},{\"id\":\"1370\"},{\"id\":\"1115\"},{\"id\":\"9547\"}],\"price\":{\"totalPrice\":{\"discountPrice\":0,\"originalPrice\":20900,\"voucherDiscount\":0,\"discount\":20900,\"currencyCode\":\"SEK\",\"currencyInfo\":{\"decimals\":2},\"fmtPrice\":{\"originalPrice\":\"SEK 209.00\",\"discountPrice\":\"0\",\"intermediatePrice\":\"0\"}},\"lineOffers\":[{\"appliedRules\":[{\"id\":\"ceb6267c43eb4c51b439bf27239c1db4\",\"endDate\":\"2020-11-12T16:00:00.000Z\",\"discountSetting\":{\"discountType\":\"PERCENTAGE\"}}]}]},\"promotions\":{\"promotionalOffers\":[{\"promotionalOffers\":[{\"startDate\":\"2020-11-05T16:00:00.000Z\",\"endDate\":\"2020-11-12T16:00:00.000Z\",\"discountSetting\":{\"discountType\":\"PERCENTAGE\",\"discountPercentage\":0}}]}],\"upcomingPromotionalOffers\":[]}}";

    private EpicGamesOfferProcessor processor;
    private OfferRepository repository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(OfferRepository.class);
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        processor = new EpicGamesOfferProcessor(new EpicGamesOfferMapper(), repository);

        var seemIds = new HashSet<>();
        Mockito.when(repository.existsById(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    var id = (String) invocation.getArguments()[0];
                    return Mono.just(!seemIds.add(id));
                });
    }

    @Test
    void onlyPersistOffersWithNewId() throws Exception {
        var saveCallCount = new AtomicInteger();
        Mockito.when(repository.save(Mockito.any()))
                .thenAnswer(invocation -> {
                    Offer offer = (Offer) invocation.getArguments()[0];
                    saveCallCount.incrementAndGet();
                    return Mono.just(offer);
                });

        var input = Flux.just(
                newEpicGamesOffer("1"),
                newEpicGamesOffer("1"),
                newEpicGamesOffer("2"),
                newEpicGamesOffer("1")
        );

        processor.offerProcessor().apply(input).blockLast();

        Assertions.assertEquals(2, saveCallCount.intValue());
    }

    private EpicGamesOffer newEpicGamesOffer(String id) throws Exception {
        var json = TEST_OFFER.replace(ID_PLACE_HOLDER, id);
        return objectMapper.readValue(json, EpicGamesOffer.class);
    }
}