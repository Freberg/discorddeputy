package com.freberg.discorddeputy

import com.freberg.discorddeputy.constant.DiscordDeputyConstants
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.blockhound.BlockHound

@SpringBootApplication
class DiscordDeputySteamApiApplication {
    companion object {
        init {
            if (System.getProperty(DiscordDeputyConstants.SYSTEM_PROPERTY_BLOCK_HOUND_ENABLED) != null) {
                BlockHound.install()
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DiscordDeputySteamApiApplication>(*args)
}