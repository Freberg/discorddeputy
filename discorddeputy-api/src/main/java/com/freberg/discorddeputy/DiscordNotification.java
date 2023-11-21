package com.freberg.discorddeputy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;


@JsonIgnoreProperties(ignoreUnknown = true)
public record DiscordNotification(@NotNull String id,
                                  @NotNull String source,
                                  @NotNull String type,
                                  @NotNull Instant timestamp,
                                  @NotNull String title,
                                  String descriptionHeader,
                                  @NotNull String description,
                                  String url,
                                  String imageUrl,
                                  String thumbnailUrl) {
}
