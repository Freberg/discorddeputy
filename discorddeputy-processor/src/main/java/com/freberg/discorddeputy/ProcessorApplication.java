package com.freberg.discorddeputy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class ProcessorApplication {

    private final DiscordNotificationRepository repository;

    @Bean
    public Function<Flux<DiscordNotification>, Flux<DiscordNotification>> processor() {
        return notifications -> notifications.flatMap(this::persist);
    }

    private Mono<DiscordNotification> persist(DiscordNotification discordNotification) {
        return repository.insert(discordNotification)
                .doOnNext(savedNotification -> log.info("Persisted new notification with ID \"{}\" to DB from source \"{}\"",
                        savedNotification.getId(), savedNotification.getSource()))
                .onErrorResume(DuplicateKeyException.class, e -> {
                    log.info("Notification with ID \"{}\" already exists in DB. Skipping.", discordNotification.getId());
                    return Mono.empty();
                })
                .doOnError(e -> log.error("Error persisting notification with ID \"{}\" from source \"{}\"",
                        discordNotification.getId(), discordNotification.getSource(), e));
    }

    public static void main(String[] args) {
        SpringApplication.run(ProcessorApplication.class);
    }
}
