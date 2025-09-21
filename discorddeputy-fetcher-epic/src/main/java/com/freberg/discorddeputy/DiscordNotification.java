package com.freberg.discorddeputy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.time.Instant;


@SuppressWarnings("UnusedDeclaration")
@Value.Immutable
@JsonSerialize(as = ImmutableDiscordNotification.class)
@JsonDeserialize(as = ImmutableDiscordNotification.class)
public interface DiscordNotification {

    String id();

    Type getType();

    Instant timestamp();

    String title();

    String descriptionHeader();

    String description();

    String url();

    String imageUrl();

    @Nullable
    String thumbnailUrl();

    Instant startTime();

    Instant endTime();

    default String getSource() {
        return "epic_games";
    }

    enum Type {
        CURRENT_OFFER("Current Offer"),
        UPCOMING_OFFER("Upcoming Offer");

        final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }
    }
}
