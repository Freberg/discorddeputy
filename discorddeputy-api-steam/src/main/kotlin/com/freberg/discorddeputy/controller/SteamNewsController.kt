package com.freberg.discorddeputy.controller

import com.freberg.discorddeputy.message.steam.SteamNews
import com.freberg.discorddeputy.repository.StreamNewsRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/stream")
class SteamNewsController(val repository: StreamNewsRepository) {

    @GetMapping("/latestNews")
    fun getLatest(): Flux<SteamNews> {
        return repository.findAll()
    }
}