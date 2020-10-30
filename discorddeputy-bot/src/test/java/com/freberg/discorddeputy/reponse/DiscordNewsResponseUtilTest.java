package com.freberg.discorddeputy.reponse;

import com.freberg.discorddeputy.message.steam.SteamNews;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DiscordNewsResponseUtilTest {


    @Test
    void parseNewsContents() {
        String rawDescription = "{STEAM_CLAN_IMAGE}/30897473/e854b08086ac28212609d0d56b5cc9070ff60506.jpg On " +
                                "Halloween, the veil between the worlds thins, stretches, and transforms, causing " +
                                "many strange occurrences and apparitions. The barriers that protect us against the " +
                                "darkness are weakened, and on All Hallows’ Eve - and the day...";
        SteamNews steamNews = Mockito.mock(SteamNews.class);
        Mockito.when(steamNews.getContents()).thenReturn(rawDescription);

        String description = DiscordNewsResponseUtil.extractDescription(steamNews);
        String imageUrl = DiscordNewsResponseUtil.extractImageUrl(steamNews);

        Assertions.assertFalse(description.contains("STEAM_CLAN_IMAGE"));
        Assertions.assertFalse(imageUrl.contains("STEAM_CLAN_IMAGE"));
    }


}