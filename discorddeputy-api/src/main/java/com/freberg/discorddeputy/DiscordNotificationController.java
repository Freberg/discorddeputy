package com.freberg.discorddeputy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.time.Instant;
import java.time.Period;

@RestController
public class DiscordNotificationController {

    private final DiscordNotificationRepository repository;

    public DiscordNotificationController(DiscordNotificationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/currentOffers")
    public List<DiscordNotification> getCurrentOffers() {
        return repository.findCurrentOffers(Instant.now().toString());
    }

    @GetMapping("/upcomingOffers")
    public List<DiscordNotification> getUpcomingOffers() {
        return repository.findUpcomingOffers(Instant.now().toString());
    }

    @GetMapping("/latestNews")
    public List<DiscordNotification> getLatestNews() {
        return repository.findLatestNews(Instant.now().minus(Period.ofDays(7)).toString());
    }
}
