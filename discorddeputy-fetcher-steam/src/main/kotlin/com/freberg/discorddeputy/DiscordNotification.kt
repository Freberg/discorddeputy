package com.freberg.discorddeputy

import java.time.Instant

@Suppress("unused")
data class DiscordNotification(
    val id: String,
    val timestamp: Instant,
    val title: String,
    val description: String,
    val url: String,
    val source: String = "steam",
    val type: String = "news"
)