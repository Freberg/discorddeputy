package com.freberg.discorddeputy;

import com.freberg.discorddeputy.model.DiscordNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
@SuppressWarnings("unused")
public class NotifierBotApplication {

    private final DiscordNotifier discordNotifier;

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
