package com.freberg.discorddeputy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
        return notifications -> notifications
                .filterWhen(notification -> repository.existsById(notification.getId()).map(b -> !b))
                .flatMap(this::persist);
    }

    private Mono<DiscordNotification> persist(DiscordNotification discordNotification) {
        log.info("Persisted new notification with ID \"{}\" to DB from source \"{}\"", discordNotification.getId(),
                discordNotification.getSource());
        return repository.save(discordNotification);
    }

    public static void main(String[] args) {
        SpringApplication.run(ProcessorApplication.class);
    }
}
