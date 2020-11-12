package com.freberg.discorddeputy.message;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freberg.discorddeputy.json.epic.EpicGamesOffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EpicGamesOfferSerDeTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void verifySerDe20201018Data() throws IOException {
        String json = Optional.of(getClass().getClassLoader())
                              .map(loader -> loader.getResource("json/epic/epicGamesOffers-2020-10-18.json"))
                              .map(URL::getFile)
                              .map(File::new)
                              .map(File::toPath)
                              .map(path -> {
                                  try {
                                      return Files.readString(path);
                                  } catch (IOException e) {
                                      return null;
                                  }
                              })
                              .orElseThrow(() -> new IOException("Test file could not be found"));

        List<EpicGamesOffer> offers = objectMapper.readValue(json, new TypeReference<>() {
        });
        Assertions.assertEquals(4, offers.size());
        String serJson = objectMapper.writeValueAsString(offers);
        List<EpicGamesOffer> deOffers = objectMapper.readValue(serJson, new TypeReference<>() {});
        Assertions.assertEquals(4, deOffers.size());
    }
}