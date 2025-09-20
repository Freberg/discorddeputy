package com.freberg.discorddeputy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.Period;

@RestController
@SpringBootApplication
@SuppressWarnings("unused")
public class ApiApplication {

    private final DiscordNotificationRepository repository;

    public ApiApplication(DiscordNotificationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/currentOffers")
    public Flux<DiscordNotification> getCurrentOffers() {
        return repository.findCurrentOffers(Instant.now().toString());
    }

    @GetMapping("/upcomingOffers")
    public Flux<DiscordNotification> getUpcomingOffers() {
        return repository.findUpcomingOffers(Instant.now().toString());
    }

    @GetMapping("/latestNews")
    public Flux<DiscordNotification> getLatestNews() {
        return repository.findLatestNews(Instant.now().minus(Period.ofDays(7)).toString());
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class);
    }
}
