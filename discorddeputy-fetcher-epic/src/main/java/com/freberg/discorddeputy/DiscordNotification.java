package com.freberg.discorddeputy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.Instant;


@SuppressWarnings("UnusedDeclaration")
@Value.Immutable
@JsonSerialize(as = ImmutableDiscordNotification.class)
@JsonDeserialize(as = ImmutableDiscordNotification.class)
interface DiscordNotification {

    String id();

    Type getType();

    Instant timestamp();

    String title();

    String descriptionHeader();

    String description();

    String url();

    String imageUrl();

    String thumbNailUrl();

    Instant startTime();

    Instant endTime();

    default String getSource() {
        return "epic_games";
    }

    enum Type {
        CURRENT_OFFER("Current Offer"),
        UPCOMING_OFFER("Upcoming Offer");

        String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }
    }
}
