package com.freberg.discorddeputy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NonNull;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DiscordNotification(@NonNull String id,
                                  @NonNull String source,
                                  @NonNull String type,
                                  @NonNull Instant timestamp,
                                  @NonNull String title,
                                  String descriptionHeader,
                                  @NonNull String description,
                                  String url,
                                  String imageUrl,
                                  String thumbnailUrl) {
}
