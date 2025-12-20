package com.freberg.discorddeputy;

import com.freberg.discorddeputy.api.DiscordNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.cloud.function.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.datatype.joda.JodaModule;
import tools.jackson.module.kotlin.KotlinModule;

import java.util.function.Consumer;

@SpringBootApplication
public class NotifierBotApplication {

    private static final Logger log = LoggerFactory.getLogger(NotifierBotApplication.class);
    private final DiscordNotifier discordNotifier;

    public NotifierBotApplication(DiscordNotifier discordNotifier) {
        this.discordNotifier = discordNotifier;
    }

    @Bean
    public Consumer<DiscordNotification> sink() {
        return notification -> {
            log.info("Received notification with ID \"{}\" from \"{}\"", notification.getId(), notification.getTitle());
            discordNotifier.onNewNotification(notification);
        };
    }

    /*
    Workaround for bug in spring-cloud-function where the wrong kotlin module is being used, can be removed once fixed
    https://github.com/spring-cloud/spring-cloud-function/issues/1319
     */
    @Bean
    public JsonMapper jsonMapper() {
        var jackson3Mapper = tools.jackson.databind.json.JsonMapper.builder()
                .addModule(new KotlinModule.Builder().build())
                .addModule(new JodaModule())
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, false)
                .build();
        return new JacksonMapper(jackson3Mapper);
    }

    public static void main(String[] args) {
        SpringApplication.run(NotifierBotApplication.class);
    }
}
