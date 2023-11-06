package com.freberg.discorddeputy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

import static com.freberg.discorddeputy.constant.DiscordDeputyConstants.SYSTEM_PROPERTY_BLOCK_HOUND_ENABLED;

@SpringBootApplication
@Slf4j
public class DiscordDeputyEpicGamesFetcherApplication {

    static {
        if (System.getProperty(SYSTEM_PROPERTY_BLOCK_HOUND_ENABLED) != null) {
            BlockHound.install();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscordDeputyEpicGamesFetcherApplication.class, args);
    }
}
