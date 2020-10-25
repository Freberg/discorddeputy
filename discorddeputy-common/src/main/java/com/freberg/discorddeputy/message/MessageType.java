package com.freberg.discorddeputy.message;

import com.freberg.discorddeputy.message.epic.EpicGamesOffer;
import com.freberg.discorddeputy.message.steam.SteamNews;

public enum MessageType {
    EPIC_GAMES_OFFER(EpicGamesOffer.class),
    STEAM_NEWS(SteamNews.class);

    Class<?> messageClazz;

    MessageType(Class<?> messageClazz) {
        this.messageClazz = messageClazz;
    }

    public Class<?> getMessageClazz() {
        return messageClazz;
    }
}
