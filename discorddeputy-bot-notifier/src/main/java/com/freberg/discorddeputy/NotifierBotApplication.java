package com.freberg.discorddeputy;

import com.freberg.discorddeputy.api.DiscordNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

    public static void main(String[] args) {
        SpringApplication.run(NotifierBotApplication.class);
    }
}
