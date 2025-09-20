package com.freberg.discorddeputy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@SpringBootApplication
public class ProcessorApplication {

    private static final Logger log = LoggerFactory.getLogger(ProcessorApplication.class);
    private final DiscordNotificationRepository repository;

    public ProcessorApplication(DiscordNotificationRepository repository) {
        this.repository = repository;
    }

    @Bean
    public Function<Flux<DiscordNotification>, Flux<DiscordNotification>> processor() {
        return notifications -> notifications.flatMap(this::persist);
    }

    private Mono<DiscordNotification> persist(DiscordNotification discordNotification) {
        return repository.insert(discordNotification)
                .doOnNext(savedNotification -> log.info("Persisted new notification with ID \"{}\" to DB from source \"{}\"",
                        savedNotification.id(), savedNotification.source()))
                .onErrorResume(DuplicateKeyException.class, e -> {
                    log.info("Notification with ID \"{}\" already exists in DB. Skipping.", discordNotification.id());
                    return Mono.empty();
                })
                .doOnError(e -> log.error("Error persisting notification with ID \"{}\" from source \"{}\"",
                        discordNotification.id(), discordNotification.source(), e));
    }

    public static void main(String[] args) {
        SpringApplication.run(ProcessorApplication.class);
    }
}
